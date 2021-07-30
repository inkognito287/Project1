package com.example.qrreader.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrreader.*
import com.example.qrreader.fragment.ImageFragment
import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityImageBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class BarcodeScanActivity : AppCompatActivity() {


    lateinit var code: String

    private lateinit var textureView: PreviewView
    private lateinit var bitmap: Bitmap
    lateinit var binding:ActivityImageBinding

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        code="Не найден"





        binding.button.setOnClickListener() {


            //mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            val bottomFragment = ImageFragment()
            val bundle = Bundle()
            bundle.putString("code", code)
            bottomFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.containerBottomSheet, bottomFragment)
                .commit()


            val bottomSheetBehaviour =
                BottomSheetBehavior.from(findViewById(R.id.containerBottomSheet))

            bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED)
            // button.isClickable = false

            //finish()
        }



        binding.buttonHistory2.setOnClickListener(){
            setResult(1,intent)
            intent.putExtra("fragment",1)
            finish()
        }
        binding.captureActivityButtonSetting2.setOnClickListener(){

            setResult(1,intent)
            intent.putExtra("fragment",2)
            finish()
        }
        binding.button2.setOnClickListener(){
            finish()
        }





        // Request camera permissions if needed
        if (isCameraPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMS_CAMERA)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMS_CAMERA) {
            if (isCameraPermissionGranted()) {
                startCamera()
            }
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // CameraProvider
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
        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

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
                      result.barcodes.forEach{
                          code=it.rawValue.toString()
                      }

                        //setResult(RESULT_OK, prepareIntentBarcodeDetected(result.barcodes.mapNotNull { it.rawValue }))
                      //  finish()
                    }
                })
            }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
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

    companion object {
        private const val REQUEST_CODE_PERMS_CAMERA = 1341
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}