package com.viktoriastoycheva.manicurear.ar

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandTrackingHelper(
    context: Context,
    private val resultListener: (HandLandmarkerResult) -> Unit
) {
    private var handLandmarker: HandLandmarker? = null

    init {
        setupHandLandmarker(context)
    }

    private fun setupHandLandmarker(context: Context) {
        val baseOptionsBuilder = BaseOptions.builder()
            .setModelAssetPath("hand_landmarker.task")

        val optionsBuilder = HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptionsBuilder.build())
            .setMinHandDetectionConfidence(0.5f)
            .setMinHandPresenceConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, _ ->
                // ПРЕМАХВАМЕ проверката isNotEmpty()
                // Искаме да пращаме резултата ВИНАГИ, дори и да е празен,
                // за да знае OverlayView кога да изчисти ноктите.
                resultListener(result)
            }

        handLandmarker = HandLandmarker.createFromOptions(context, optionsBuilder.build())
    }

    fun detectHands(bitmap: Bitmap, timestamp: Long) {
        try {
            // MediaPipe работи най-добре, когато изображението е правилно ориентирано.
            // Тъй като вече коригираме координатите в OverlayView, тук просто подаваме Bitmap-а.
            val mpImage = BitmapImageBuilder(bitmap).build()
            handLandmarker?.detectAsync(mpImage, timestamp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Добра практика е да добавим метод за затваряне, за да не хабим ресурси
    fun close() {
        handLandmarker?.close()
    }
}