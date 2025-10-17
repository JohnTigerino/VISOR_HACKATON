package com.bionica.visor_prueba3

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bionica.visor_prueba3.databinding.ActivityAnalisisBinding
import com.bionica.visor_prueba3.ml.ImageClassifierHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnalisisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalisisBinding
    private lateinit var classifier: ImageClassifierHelper

    // Galería (Photo Picker)
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let { onImageReady(it) }
        }

    // Permiso de cámara
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCamera() else
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }

    // Cámara (bitmap preview)
    private val takePicturePreview =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
            bmp?.let {
                showPickedState()
                binding.imgPreview.setImageBitmap(it)
                binding.btnAnalizar.isEnabled = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalisisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Modelo en assets/modelo_cnn.tflite
        classifier = ImageClassifierHelper(this)

        binding.btnGaleria.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCamara.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            } else openCamera()
        }

        binding.btnAnalizar.setOnClickListener {
            runInferenceFromImageView()
        }
    }

    /** Estado cuando ya hay imagen seleccionada. */
    private fun showPickedState() {
        binding.boxPick.visibility = View.GONE
        binding.imgPreview.visibility = View.VISIBLE
        binding.btnAnalizar.visibility = View.VISIBLE
    }

    /** Imagen desde galería. */
    private fun onImageReady(uri: Uri) {
        showPickedState()
        val bmp = loadBitmapFromUri(uri)
        if (bmp != null) {
            binding.imgPreview.setImageBitmap(bmp)
            binding.btnAnalizar.isEnabled = true
        } else {
            Toast.makeText(this, "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        takePicturePreview.launch(null)
    }

    /** Corre inferencia tomando el Bitmap actual del ImageView. */
    private fun runInferenceFromImageView() {
        val bmp = (binding.imgPreview.drawable as? Drawable)?.toBitmapSafely()
        if (bmp == null) {
            Toast.makeText(this, "Primero selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        // UI: progreso
        binding.btnAnalizar.isEnabled = false
        binding.progress.visibility = View.VISIBLE
        binding.cardResultado.visibility = View.GONE

        lifecycleScope.launch {
            val results = withContext(Dispatchers.Default) {
                classifier.classify(bmp)  // List<Pair<label, score>>
            }

            binding.progress.visibility = View.GONE
            binding.btnAnalizar.isEnabled = true

            if (results.isEmpty()) {
                mostrarResultado("Sin resultados")
            } else {
                val texto = results.joinToString("\n") { (label, score) ->
                    "$label — ${(score * 100).toInt()}%"
                }
                mostrarResultado(texto)
            }
        }
    }

    /** Mostrar tarjeta de resultados. */
    private fun mostrarResultado(texto: String) {
        binding.cardResultado.visibility = View.VISIBLE
        binding.tvResumen.text = texto
    }

    /** Bitmap desde Uri (API-safe). */
    private fun loadBitmapFromUri(uri: Uri): Bitmap? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/** Extensión segura: Drawable -> Bitmap. */
private fun Drawable.toBitmapSafely(): Bitmap {
    return when (this) {
        is BitmapDrawable -> this.bitmap
        else -> {
            val w = intrinsicWidth.coerceAtLeast(1)
            val h = intrinsicHeight.coerceAtLeast(1)
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bmp)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
            bmp
        }
    }
}
