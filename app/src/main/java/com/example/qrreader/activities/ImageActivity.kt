package com.example.qrreader.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.qrreader.Fragment.ImageFragment
import com.example.qrreader.QrCodeAnalyzer
import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityImageBinding
import com.example.qrreader.databinding.FragmentImageBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior


class ImageActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }

    lateinit var code: String

    private lateinit var textureView: PreviewView
    private lateinit var bitmap: Bitmap
    lateinit var binding:ActivityImageBinding

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        code = "не найден"

        // var bottomSheet = findViewById<View>(R.id.bottom_sheet);
        //     var mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)


        textureView = binding.textureView
        binding.buttonHistory2.setOnClickListener(){
            setResult(1)
            intent.putExtra("fragment",1)
            finish()
        }
        binding.captureActivityButtonSetting2.setOnClickListener(){

            setResult(1)
            intent.putExtra("fragment",2)
            finish()
        }
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

        // Request camera permissions
        if (isCameraPermissionGranted()) {
            textureView.post { startCamera() }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val cameraSelector =
            CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        val previewConfig = Preview.Builder()
            .build()

        previewConfig.setSurfaceProvider(textureView.surfaceProvider)


        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
        val executor = ContextCompat.getMainExecutor(this)
        val imageAnalyzer = ImageAnalysis.Builder().build().also {
            it.setAnalyzer(executor, QrCodeAnalyzer { qrCodes ->
                qrCodes?.forEach {
                    code = it.rawValue.toString()
                    //  Toast.makeText(this, it.rawValue, Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "QR Code detected: ${it.rawValue}.")
                }
            })
        }

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                previewConfig,
                imageCapture,
                imageAnalyzer
            )

            //Handle flash
            camera.cameraControl.enableTorch(false)
        }, executor)
    }

    private fun isCameraPermissionGranted(): Boolean {
        val selfPermission =
            ContextCompat.checkSelfPermission(baseContext, Manifest.permission.CAMERA)
        return selfPermission == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (isCameraPermissionGranted()) {
                textureView.post { startCamera() }
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}