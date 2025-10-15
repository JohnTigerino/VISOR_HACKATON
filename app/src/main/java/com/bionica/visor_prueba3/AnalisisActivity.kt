package com.bionica.visor_prueba3

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bionica.visor_prueba3.databinding.ActivityAnalisisBinding

class AnalisisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalisisBinding

    // Photo Picker (galería). En Android 13+ usa el picker nativo; en anteriores, usa fallback.
    private val pickImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let { onImageReady(it) }
        }

    // Permiso de cámara (para TakePicturePreview)
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCamera()
        }

    // Cámara (preview Bitmap). Si quieres archivo en tamaño completo, cambia a TakePicture con FileProvider.
    private val takePicturePreview =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
            bmp?.let {
                // Mostrar el bitmap directamente
                binding.boxPick.visibility = View.GONE
                binding.imgPreview.setImageBitmap(it)
                binding.imgPreview.visibility = View.VISIBLE
                binding.btnAnalizar.visibility = View.VISIBLE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalisisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar (opcional)
        setSupportActionBar(binding.toolbar)

        // Abrir galería
        binding.btnGaleria.setOnClickListener {
            pickImage.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        // Abrir cámara
        binding.btnCamara.setOnClickListener {
            // Android 13+ no requiere declarar permiso en manifest para el Photo Picker, pero la cámara sí.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            } else {
                openCamera()
            }
        }

        // Analizar con IA
        binding.btnAnalizar.setOnClickListener {
            // Llamá a tu CNN acá y reemplazá el texto con el resultado real
            mostrarResultado("Roya del frijol: Puntos amarillos que se oscurecen..." +
                    "\nTratamiento: Infusión de ajo o fungicida a base de leche de vaca...")
        }
    }

    // al elegir imagen (galería o cámara)
    private fun onImageReady(uri: Uri) {
        binding.boxPick.visibility = View.GONE
        binding.imgPreview.setImageURI(uri)
        binding.imgPreview.visibility = View.VISIBLE
        binding.btnAnalizar.visibility = View.VISIBLE
    }

    private fun openCamera() {
        takePicturePreview.launch(null)
    }

    private fun mostrarResultado(texto: String) {
        binding.cardResultado.visibility = View.VISIBLE
        binding.tvResumen.text = texto
    }
}
