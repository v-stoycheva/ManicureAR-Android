package com.viktoriastoycheva.manicurear

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.viktoriastoycheva.manicurear.ar.DesignBottomSheet
import com.viktoriastoycheva.manicurear.ar.HandTrackingHelper
import com.viktoriastoycheva.manicurear.ar.OverlayView
import com.viktoriastoycheva.manicurear.models.ArDesign
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.network.ApiService
import com.viktoriastoycheva.manicurear.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class CameraActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var tvStatus: TextView
    private lateinit var overlayView: OverlayView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var handTrackingHelper: HandTrackingHelper
    private lateinit var apiService: ApiService
    private lateinit var sessionManager: SessionManager
    private lateinit var btnOpenDesigns: Button
    private lateinit var btnConfirmSelection: Button
    private lateinit var btnFavorite: ImageButton

    private var currentDesignId: Long = -1L
    private var selectedDesignObject: ArDesign? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val isBookingMode = intent.getBooleanExtra("IS_BOOKING_MODE", false)
        sessionManager = SessionManager(this)
        apiService = ApiClient.instance

        viewFinder = findViewById(R.id.viewFinder)
        tvStatus = findViewById(R.id.tvStatus)
        overlayView = findViewById(R.id.overlayView)
        btnOpenDesigns = findViewById(R.id.btnOpenDesigns)
        btnConfirmSelection = findViewById(R.id.btnConfirmSelection)
        btnFavorite = findViewById(R.id.btnFavorite)

        cameraExecutor = Executors.newSingleThreadExecutor()

        btnConfirmSelection.visibility = if (isBookingMode) View.VISIBLE else View.GONE

        btnOpenDesigns.setOnClickListener {
            val bottomSheet = DesignBottomSheet { selectedDesign ->
                selectedDesignObject = selectedDesign
                currentDesignId = selectedDesign.arDesignId.toLong()
                checkIfFavorite(sessionManager.getUserId(), currentDesignId)

                Glide.with(this)
                    .asBitmap()
                    .load(selectedDesign.filePath)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            overlayView.updateNailTexture(resource)
                            Toast.makeText(this@CameraActivity, "Design loaded!", Toast.LENGTH_SHORT).show()
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            // Ако влезе тук, Glide не може да свали картинката от Firebase
                            Toast.makeText(this@CameraActivity, "Failed to download AR texture", Toast.LENGTH_SHORT).show()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }
            bottomSheet.show(supportFragmentManager, "DesignBottomSheet")
        }

        btnFavorite.setOnClickListener {
            val userId = sessionManager.getUserId()
            if (currentDesignId != -1L && userId != -1L) {
                apiService.toggleFavorite(userId, currentDesignId).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            checkIfFavorite(userId, currentDesignId)
                            Toast.makeText(this@CameraActivity, "Updated!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Ако влезе тук, значи сървърът отказва заявката (напр. грешка 404 или 500)
                            Toast.makeText(this@CameraActivity, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // Ако влезе тук, значи Android не вижда IntelliJ сървъра
                        Toast.makeText(this@CameraActivity, "Cannot reach IntelliJ: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            } else {
                Toast.makeText(this, "Please select a design from the menu first", Toast.LENGTH_SHORT).show()
            }
        }

        btnConfirmSelection.setOnClickListener {
            if (selectedDesignObject != null) {
                val resultIntent = Intent()
                resultIntent.putExtra("SELECTED_DESIGN_NAME", selectedDesignObject?.name)
                resultIntent.putExtra("SELECTED_DESIGN_ID", currentDesignId)

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please select a design from the menu first", Toast.LENGTH_SHORT).show()
            }
        }

        setupHandTracking()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun checkIfFavorite(userId: Long, designId: Long) {
        apiService.getFavoriteDesigns(userId).enqueue(object : Callback<List<ArDesign>> {
            override fun onResponse(call: Call<List<ArDesign>>, response: Response<List<ArDesign>>) {
                if (response.isSuccessful) {
                    val favorites = response.body() ?: emptyList()
                    // Сравняваме ID-тата, за да разберем дали дизайнът е в любими
                    val isFav = favorites.any { it.arDesignId.toLong() == designId }
                    btnFavorite.setImageResource(
                        if (isFav) R.drawable.ic_favorite_filled else R.drawable.heart_white
                    )
                }
            }

            override fun onFailure(call: Call<List<ArDesign>>, t: Throwable) {
                android.util.Log.e("NETWORK_ERROR", "Failed to connect to server: ${t.message}")

                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Connection lost. Heart status may be outdated.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun setupHandTracking() {
        handTrackingHelper = HandTrackingHelper(this) { result ->
            runOnUiThread {
                if (result != null && result.landmarks().isNotEmpty()) {
                    tvStatus.text = "HAND DETECTED! \u2705"
                    overlayView.setResults(result)
                } else {
                    tvStatus.text = "Searching for hand..."
                    overlayView.setResults(null)
                }
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            val resolutionSelector = ResolutionSelector.Builder()
                .setResolutionStrategy(ResolutionStrategy(Size(1280, 720), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER))
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analyzer ->
                    analyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                        val bitmap = imageProxy.toBitmap()
                        if (bitmap != null) {
                            handTrackingHelper.detectHands(bitmap, System.currentTimeMillis())
                        }
                        imageProxy.close()
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer)
            } catch (e: Exception) {
                Toast.makeText(this, "Camera error", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        handTrackingHelper.close()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}