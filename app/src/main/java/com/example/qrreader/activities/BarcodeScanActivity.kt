package com.example.qrreader.activities

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrreader.*
import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityImageBinding
import com.example.qrreader.fragment.ImageFragment
import com.example.qrreader.fragment.myAdapter
import com.example.qrreader.fragment.myAdapterUpdate
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.service.MyService
//import com.example.qrreader.service.MyService
import com.example.qrreader.singletones.MySingleton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class BarcodeScanActivity : AppCompatActivity() {


    lateinit var code: String
    lateinit var binding: ActivityImageBinding
    lateinit var myFunctions: Functions
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myFunctions = Functions(this)
        code = "не найден"

        MySingleton.countActivity = 1
        binding.button.setOnClickListener {
            MySingleton.dontGoOut = 0
            binding.button.isClickable = false
            //  if (code != "не найден") {
            MySingleton.temporaryImage = findViewById<PreviewView>(R.id.preview)?.bitmap!!

            val barcodeBitmapAnalyzer = BarcodeBitmapAnalyzer(this)
            barcodeBitmapAnalyzer.scanBarcodes(MySingleton.temporaryImage, code)


        }


        binding.button2.setOnClickListener {
            finish()
        }

        if (isCameraPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMS_CAMERA
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    override fun onBackPressed() {
        val bottomSheetBehaviour = BottomSheetBehavior.from(findViewById(R.id.containerBottomSheet))
        if ((bottomSheetBehaviour.state == 4 || bottomSheetBehaviour.state == BottomSheetBehavior.STATE_HIDDEN) && MySingleton.dontGoOut == 0) {
            MySingleton.completedPages.clear()
            MySingleton.image = java.util.ArrayList()
            MySingleton.title = java.util.ArrayList()
            MySingleton.text = String()
            MySingleton.image = java.util.ArrayList()
            MySingleton.day = java.util.ArrayList()
            MySingleton.time = java.util.ArrayList()
            MySingleton.status = java.util.ArrayList()
            MySingleton.text = String()
            MySingleton.newSession = true
            super.onBackPressed()
        } else if (MySingleton.dontGoOut == 1) {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
            MySingleton.dontGoOut = 2

        } else if (MySingleton.dontGoOut == 2) {
            finish()
            MySingleton.completedPages.clear()
            MySingleton.image = java.util.ArrayList()
            MySingleton.title = java.util.ArrayList()
            MySingleton.text = String()
            MySingleton.image = java.util.ArrayList()
            MySingleton.day = java.util.ArrayList()
            MySingleton.time = java.util.ArrayList()
            MySingleton.status = java.util.ArrayList()
            MySingleton.text = String()
            MySingleton.newSession = true
        } else {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
            MySingleton.dontGoOut = 0
            binding.button.isClickable = true
            MySingleton.completedPages[MySingleton.currentPage - 1] = false
            MySingleton.image[MySingleton.currentPage - 1] = null
            MySingleton.title[MySingleton.currentPage - 1] = null
            MySingleton.text = null
            MySingleton.status[MySingleton.currentPage - 1] = null
            MySingleton.day[MySingleton.currentPage - 1] = null
            MySingleton.time[MySingleton.currentPage - 1] = null
            MySingleton.completedPages = java.util.ArrayList()
            MySingleton.image = java.util.ArrayList()
            MySingleton.title = java.util.ArrayList()
            MySingleton.text = String()
            MySingleton.image = java.util.ArrayList()
            MySingleton.day = java.util.ArrayList()
            MySingleton.time = java.util.ArrayList()
            MySingleton.status = java.util.ArrayList()
            MySingleton.text = String()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMS_CAMERA) {
            if (isCameraPermissionGranted()) {
                startCamera()
            } else {
                finish()
            }
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({

            cameraProvider = cameraProviderFuture.get()

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        // CameraProvider
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(binding.preview.createSurfaceProvider())

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { binding.preview.display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

        // ImageAnalysis
        val imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(cameraExecutor, BarcodeAnalyzer { result ->
                    // update UI
                    binding.barcodeOverlay.update(result)
                    // send result if not empty
                    if (result.barcodes.isNotEmpty() && !isFinishing) {
                        result.barcodes.forEach {
                            code = it.rawValue.toString()
                        }

                    }
                })
            }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
        } catch (exc: Exception) {
            Log.e("ImageCaptureActivity", "error communicating with camera", exc)
            Toast.makeText(this, "error_communicating_with_device_camera", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }


    private fun isMyServiceRunning(myClass: Class<MyService>): Boolean {

        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager


        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {


            if (myClass.name.equals(service.service.className)) {

                return true

            }

        }
        return false
    }


    override fun onResume() {
        super.onResume()
        MySingleton.applicationIsActive = true
        Log.d("MyLog", "Barcode is active=" + MySingleton.applicationIsActive)
        if (isMyServiceRunning(MyService::class.java)) {
            stopService(Intent(this, MyService::class.java))
        }

    }

    override fun onPause() {
        super.onPause()
        MySingleton.applicationIsActive = false
        Log.d("MyLog", "Barcode is active=" + MySingleton.applicationIsActive)
    }

    companion object {
        private const val REQUEST_CODE_PERMS_CAMERA = 1341
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    override fun onStop() {

        Thread() {

            if (!isMyServiceRunning(MyService::class.java) && !MySingleton.applicationIsActive && myFunctions.notAllSent()) {
                startService(Intent(this, MyService::class.java))
            }

        }.start()
        super.onStop()
    }
}