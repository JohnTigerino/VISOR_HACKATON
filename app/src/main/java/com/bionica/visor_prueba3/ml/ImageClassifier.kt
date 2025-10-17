package com.bionica.visor_prueba3.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.ImageClassifierOptions
import org.tensorflow.lite.task.vision.classifier.Classifications

enum class Delegate { CPU, NNAPI, GPU }

data class Prediction(
    val label: String,
    val displayName: String,
    val score: Float,
    val index: Int
)

class ImageClassifierHelper(
    private val context: Context,
    private val modelPath: String = "modelo_cnn.tflite",
    private val maxResults: Int = 3,
    /** Si es null: no se aplica filtro por umbral. Evita listas vacías. */
    private val scoreThreshold: Float? = null,
    private val numThreads: Int = 4,
    private val delegate: Delegate = Delegate.CPU
) : AutoCloseable {

    private val classifier: ImageClassifier by lazy {
        val baseBuilder = BaseOptions.builder().setNumThreads(numThreads)
        when (delegate) {
            Delegate.NNAPI -> baseBuilder.useNnapi()
            Delegate.GPU   -> baseBuilder.useGpu() // requiere dependencia tflite-gpu
            else -> Unit
        }
        val baseOptions = baseBuilder.build()

        val optBuilder = ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)

        // Sólo aplicamos threshold si el caller lo pide
        scoreThreshold?.let { optBuilder.setScoreThreshold(it) }

        ImageClassifier.createFromFileAndOptions(context, modelPath, optBuilder.build())
    }

    fun classify(bitmap: Bitmap): List<Prediction> {
        val tensorImage = TensorImage.fromBitmap(bitmap)
        val results: List<Classifications> = try {
            classifier.classify(tensorImage)
        } catch (_: Exception) {
            return emptyList()
        }

        val first = results.firstOrNull() ?: return emptyList()

        return first.categories
            .asSequence()
            .sortedByDescending(Category::getScore)
            .mapIndexed { ix, cat ->
                val raw = cat.label.orEmpty()
                val display = cat.displayName?.takeIf { it.isNotBlank() } ?: raw.ifBlank { "Clase #$ix" }
                Prediction(
                    label = raw,
                    displayName = display,
                    score = cat.score,
                    index = cat.index
                )
            }
            .toList()
    }

    /** Devuelve exactamente lo que espera tu Activity: (textoParaUI, score) */
    fun classifyTopLabels(bitmap: Bitmap): List<Pair<String, Float>> =
        classify(bitmap).map { it.displayName to it.score }

    override fun close() {
        try { classifier.close() } catch (_: Exception) { }
    }
}
