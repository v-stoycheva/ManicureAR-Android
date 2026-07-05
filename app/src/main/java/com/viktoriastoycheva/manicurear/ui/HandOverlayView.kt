package com.viktoriastoycheva.manicurear.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

class HandOverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 10f
    }

    private var results: HandLandmarkerResult? = null

    fun setResults(handLandmarkerResult: HandLandmarkerResult) {
        results = handLandmarkerResult
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        results?.let { result ->
            for (landmarks in result.landmarks()) {
                for (landmark in landmarks) {
                    val x = landmark.x() * width
                    val y = landmark.y() * height
                    canvas.drawCircle(x, y, 10f, paint)
                }
            }
        }
    }
}