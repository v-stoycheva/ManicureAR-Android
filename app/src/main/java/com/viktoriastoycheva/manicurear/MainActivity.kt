package com.viktoriastoycheva.manicurear

import android.graphics.Bitmap
import android.os.Bundle
import android.view.PixelCopy
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.viktoriastoycheva.manicurear.ar.HandTrackingHelper
import com.viktoriastoycheva.manicurear.ui.HandOverlayView
import io.github.sceneview.ar.ArSceneView

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    private lateinit var handOverlay: HandOverlayView
    private lateinit var placeButton: ExtendedFloatingActionButton
    private lateinit var handTrackingHelper: HandTrackingHelper

    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Инициализация на View елементите
        sceneView = findViewById(R.id.sceneView)
        handOverlay = findViewById(R.id.handOverlay)
        placeButton = findViewById(R.id.placeButton)

        // 2. Инициализация на MediaPipe помощника
        handTrackingHelper = HandTrackingHelper(this) { result ->
            // Връщаме се в главния thread, за да обновим UI
            runOnUiThread {
                handOverlay.setResults(result)
            }
        }

        // 3. Настройка на AR сцената
        sceneView.apply {
            planeRenderer.isVisible = true // Показва точките на ARCore за повърхност
        }

        // 4. Слушател за всеки кадър (Frame)
        sceneView.onFrame = { frameTime ->
            if (!isProcessing) {
                captureFrameForAnalysis()
            }
        }

        placeButton.setOnClickListener {
            // Тук по-късно ще добавим логиката за смяна на лака
        }
    }

    private fun captureFrameForAnalysis() {
        // Проверка за валидни размери на екрана
        if (sceneView.width <= 0 || sceneView.height <= 0) return

        isProcessing = true

        // Създаваме Bitmap, в който ще копираме кадъра от камерата
        val bitmap = Bitmap.createBitmap(sceneView.width, sceneView.height, Bitmap.Config.ARGB_8888)

        // Използваме PixelCopy за висококачествено заснемане на AR сцената
        PixelCopy.request(sceneView, bitmap, { result ->
            if (result == PixelCopy.SUCCESS) {
                handTrackingHelper.detectHands(bitmap, System.currentTimeMillis())
            }
            isProcessing = false
        }, sceneView.handler)
    }

    // SceneView управлява жизнения цикъл автоматично, 
    // но можем да добавим логове тук за дебъгване ако е нужно.
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}