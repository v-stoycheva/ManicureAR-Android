package com.viktoriastoycheva.manicurear.ar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.viktoriastoycheva.manicurear.R
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlin.math.atan2

class OverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var results: HandLandmarkerResult? = null
    private var nailBitmap: Bitmap? = null

    // Пазим точките за лека плавност (премахва треперенето)
    private var smoothedPoints = mutableMapOf<Int, Pair<Float, Float>>()
    private val smoothingFactor = 0.85f // Достатъчно бързо, но стабилно

    init {
        try {
            // Зареждаме текстурата
            val original = BitmapFactory.decodeResource(resources, R.drawable.nail_texture)
            nailBitmap = original
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setResults(handLandmarkerResult: HandLandmarkerResult?) {
        results = handLandmarkerResult
        // Ако няма ръка, изчистваме всичко веднага
        if (handLandmarkerResult == null || handLandmarkerResult.landmarks().isEmpty()) {
            smoothedPoints.clear()
        }
        invalidate()
    }

    // Метод за смяна на дизайна в реално време
    fun updateNailTexture(newBitmap: Bitmap) {
        // Оразмеряваме новия Bitmap веднага, за да не го правим в draw()
        nailBitmap = newBitmap
        invalidate() // Прерисуваме екрана
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val handLandmarks = results?.landmarks()
        if (handLandmarks.isNullOrEmpty()) return

        for (landmarks in handLandmarks) {
            val fingerMap = mapOf(
                4 to 3,  // Палец
                8 to 7,  // Показалец
                12 to 11, // Среден
                16 to 15, // Безименен
                20 to 19  // Кутре
            )

            for ((tipIndex, baseIndex) in fingerMap) {
                val tip = landmarks[tipIndex]
                val base = landmarks[baseIndex]

                // --- 1. ТРАНСФОРМАЦИЯ НА КООРДИНАТИТЕ ---
                fun getCanvasCoords(lm: com.google.mediapipe.tasks.components.containers.NormalizedLandmark): Pair<Float, Float> {
                    val rawX = (1 - lm.y()) * width
                    val rawY = lm.x() * height

                    // 1. Регулираме разпъването по X
                    val stretchFactor = 1.08f
                    val centeredX = (rawX - (width * 0.5f)) * stretchFactor + (width * 0.5f)

                    // 2. ИЗЧИСЛЯВАМЕ finalX
                    val finalX = centeredX - (width * 0.01f) // Вече няма да е сиво, ако го сложиш долу

                    // 3. ИЗЧИСЛЯВАМЕ finalY
                    val finalY = rawY - (height * 0.02f)

                    // ВАЖНО: Тук трябва да върнеш новите променливи!
                    return Pair(finalX, finalY)
                }

                val (tipX, tipY) = getCanvasCoords(tip)
                val (baseX, baseY) = getCanvasCoords(base)

                // --- 2. ФИЛТЪР ЗА ТРЕПЕРЕНЕ ---
                val prev = smoothedPoints[tipIndex] ?: Pair(tipX, tipY)
                val smoothX = prev.first + (tipX - prev.first) * smoothingFactor
                val smoothY = prev.second + (tipY - prev.second) * smoothingFactor
                smoothedPoints[tipIndex] = Pair(smoothX, smoothY)

                // --- 3. ДИНАМИЧЕН РАЗМЕР (Z-DEPTH) ---
                // tip.z() е отрицателно, когато е близо. 1.0 - (-0.1 * 4) = 1.4 (по-голямо)
                val distanceScale = (1.0f - (tip.z() * 4.0f)).coerceIn(0.7f, 2.0f)

                val finalWidth = (70 * distanceScale).toInt()
                val finalHeight = (110 * distanceScale).toInt()

                // --- 4. РОТАЦИЯ ---
                val angle = atan2((smoothY - baseY).toDouble(), (smoothX - baseX).toDouble())
                val rotationDegrees = Math.toDegrees(angle).toFloat() + 90f

                // --- 5. РИСУВАНЕ ---
                nailBitmap?.let { bitmap ->
                    val scaledNail = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)

                    val matrix = Matrix()
                    // Центрираме спрямо НОВИЯ размер
                    matrix.postTranslate(-scaledNail.width / 2f, -scaledNail.height / 2f)
                    matrix.postRotate(rotationDegrees)
                    matrix.postTranslate(smoothX, smoothY)

                    canvas.drawBitmap(scaledNail, matrix, null)
                }
            }
        }
    }
}