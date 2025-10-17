package com.bionica.visor_prueba3.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.core.TensorImage
import org.tensorflow.lite.task.core.Category
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import org.tensorflow.lite.task.vision.classifier.Classifications

class ImageClassifierHelper(
    context: Context,
    private val modelPath: String = "modelo_cnn.tflite",
    private val maxResults: Int = 3,
    private val scoreThreshold: Float = 0.30f,
    private val numThreads: Int = 4
) {
    private val classifier: ImageClassifier

    init {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(numThreads)
            // .useNnapi()  // opcional
            // .useGpu()    // opcional (si agregas tflite-gpu)
            .build()

        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(maxResults)
            .setScoreThreshold(scoreThreshold)
            .build()

        classifier = ImageClassifier.createFromFileAndOptions(context, modelPath, options)
    }

    /** Retorna pares (label, score) ordenados por confianza. */
    fun classify(bitmap: Bitmap): List<Pair<String, Float>> {
        val tensorImage: TensorImage = TensorImage.fromBitmap(bitmap)
        val results: List<Classifications> = classifier.classify(tensorImage)
        if (results.isEmpty()) return emptyList()

        val categories: List<Category> = results[0].categories

        return categories
            .sortedByDescending { cat: Category -> cat.score }
            .map { cat: Category ->
                // Usa getters Java para evitar el error del binding de propiedades
                val display = cat.getDisplayName()
                val name = cat.getCategoryName()
                val label = if (display.isNullOrBlank()) name else display
                label to cat.score
            }
    }
}
