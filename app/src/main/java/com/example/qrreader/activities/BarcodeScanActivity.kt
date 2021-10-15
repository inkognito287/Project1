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
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrreader.barcode.BarcodeAnalyzer
import com.example.qrreader.barcode.BarcodeBitmapAnalyzer
import com.example.qrreader.Functions
import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityImageBinding
import com.example.qrreader.service.MyService
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
    lateinit var mySingleton:MySingleton
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mySingleton= MySingleton()
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myFunctions = Functions(this)
        code = "не найден"
        binding.button.setOnClickListener {
            mySingleton.dontGoOut = 0
            binding.button.isClickable = false
            //  if (code != "не найден") {
            mySingleton.temporaryImage = findViewById<PreviewView>(R.id.preview)?.bitmap!!

            val barcodeBitmapAnalyzer = BarcodeBitmapAnalyzer(this)
            barcodeBitmapAnalyzer.scanBarcodes(mySingleton.temporaryImage, code)


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
        if ((bottomSheetBehaviour.state == 4 || bottomSheetBehaviour.state == BottomSheetBehavior.STATE_HIDDEN) && mySingleton.dontGoOut == 0) {

            mySingleton.completedPages.clear()
            mySingleton.image = java.util.ArrayList()
            mySingleton.title = java.util.ArrayList()
            mySingleton.text = String()
            mySingleton.image = java.util.ArrayList()
            mySingleton.day = java.util.ArrayList()
            mySingleton.time = java.util.ArrayList()
            mySingleton.status = java.util.ArrayList()
            mySingleton.text = String()
            mySingleton.newSession = true

            super.onBackPressed()
        } else if (mySingleton.dontGoOut == 1) {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
            mySingleton.dontGoOut = 2

        } else if (mySingleton.dontGoOut == 2) {
            finish()
            mySingleton.completedPages.clear()
            mySingleton.image = java.util.ArrayList()
            mySingleton.title = java.util.ArrayList()
            mySingleton.text = String()
            mySingleton.image = java.util.ArrayList()
            mySingleton.day = java.util.ArrayList()
            mySingleton.time = java.util.ArrayList()
            mySingleton.status = java.util.ArrayList()
            mySingleton.text = String()
            mySingleton.newSession = true
        } else {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
            mySingleton.dontGoOut = 0
            binding.button.isClickable = true
            mySingleton.completedPages[mySingleton.currentPage - 1] = false
            mySingleton.image[mySingleton.currentPage - 1] = null
            mySingleton.title[mySingleton.currentPage - 1] = null
            mySingleton.text = null
            mySingleton.status[mySingleton.currentPage - 1] = null
            mySingleton.day[mySingleton.currentPage - 1] = null
            mySingleton.time[mySingleton.currentPage - 1] = null
            mySingleton.completedPages = java.util.ArrayList()
            mySingleton.image = java.util.ArrayList()
            mySingleton.title = java.util.ArrayList()
            mySingleton.text = String()
            mySingleton.image = java.util.ArrayList()
            mySingleton.day = java.util.ArrayList()
            mySingleton.time = java.util.ArrayList()
            mySingleton.status = java.util.ArrayList()
            mySingleton.text = String()
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
        preview.setSurfaceProvider(binding.preview.surfaceProvider)

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { binding.preview.display.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)

        // ImageAnalysis
        val imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
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
        mySingleton.applicationIsActive = true
        Log.d("MyLog", "Barcode is active=" + mySingleton.applicationIsActive)
        if (isMyServiceRunning(MyService::class.java)) {
            stopService(Intent(this, MyService::class.java))
        }

    }

    override fun onPause() {
        super.onPause()
        mySingleton.applicationIsActive = false
        Log.d("MyLog", "Barcode is active=" + mySingleton.applicationIsActive)
    }

    companion object {
        private const val REQUEST_CODE_PERMS_CAMERA = 1341
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    override fun onStop() {

        Thread() {

            if (!isMyServiceRunning(MyService::class.java) && !mySingleton.applicationIsActive && myFunctions.notAllSent()) {
                startService(Intent(this, MyService::class.java))
            }

        }.start()
        super.onStop()
    }
}