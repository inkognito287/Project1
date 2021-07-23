package com.example.qrreader

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ImageActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 10
    }
    lateinit var code:String

    private lateinit var textureView: PreviewView
    private lateinit var bitmap:Bitmap
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        textureView = findViewById(R.id.textureView)
        var button=findViewById<Button>(R.id.button)
        button.setOnClickListener(){
            bitmap = textureView.bitmap!!

            var imageView=ImageView(this)
            imageView.setImageBitmap(bitmap)
            findViewById<ConstraintLayout>(R.id.dad).addView(imageView)
            intent.putExtra("code",code)
            setResult(28,intent)
            finish()
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
            // We want to show input from back camera of the device
//            .setTargetResolution(Size(400,400))
            .build()

        previewConfig.setSurfaceProvider(textureView.surfaceProvider)


        val imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
//            .setTargetResolution(Size(400,400))
            // We request aspect ratio but no resolution to match preview config, but letting
            // CameraX optimize for whatever specific resolution best fits requested capture mode
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            .build()
        val executor = ContextCompat.getMainExecutor(this)
        val imageAnalyzer = ImageAnalysis.Builder().build().also {
            it.setAnalyzer(executor, QrCodeAnalyzer { qrCodes ->
                qrCodes?.forEach {
                    code=it.rawValue.toString()
                    Toast.makeText(this, it.rawValue, Toast.LENGTH_SHORT).show()
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
            camera.cameraControl.enableTorch(true)
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