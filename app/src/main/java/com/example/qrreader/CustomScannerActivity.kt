package com.example.qrreader

import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import com.example.qrreader.databinding.FragmentMainBinding
import com.journeyapps.barcodescanner.CameraPreview
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener
import com.journeyapps.barcodescanner.ViewfinderView
import java.util.*

class CustomScannerActivity : Activity(), TorchListener {


    private var capture: CaptureManager? = null
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    lateinit var binding:FragmentMainBinding
    lateinit var viewfinderView: ViewfinderView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        barcodeScannerView = findViewById(R.id.scanner)
        barcodeScannerView.setTorchListener(this)
        viewfinderView = findViewById(R.id.zxing_viewfinder_view)


        capture = CaptureManager(this, barcodeScannerView)
        capture!!.initializeFromIntent(intent, savedInstanceState)
        capture!!.setShowMissingCameraPermissionDialog(false)
        capture!!.decode()
        changeMaskColor(null)
        changeLaserVisibility(true)
    }

    override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture!!.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }



    fun changeMaskColor(view: View?) {
        val rnd = Random()
        val color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        viewfinderView!!.setMaskColor(color)
    }

    fun changeLaserVisibility(visible: Boolean) {
        viewfinderView!!.setLaserVisibility(!visible)
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        capture!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onTorchOn() {
        TODO("Not yet implemented")
    }

    override fun onTorchOff() {
        TODO("Not yet implemented")
    }
}
