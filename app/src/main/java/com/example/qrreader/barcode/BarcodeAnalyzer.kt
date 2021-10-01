package com.example.qrreader.barcode

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.qrreader.data.ScanResult
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean


class BarcodeAnalyzer(val onBarCodeDetected: (result: ScanResult) -> Unit)
    : ImageAnalysis.Analyzer {

    private var isBusy = AtomicBoolean(false)


    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(image: ImageProxy) {
        if (isBusy.compareAndSet(false, true)) {
            val visionImage = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)
            val img=image.image;
            BarcodeScanning.getClient().process(visionImage)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { result ->
                            onBarCodeDetected(ScanResult(result, image.width, image.height))
                        }
                    } else {
                        Log.w("BarcodeAnalyzer", "failed to scan image: ${task.exception?.message}")
                    }
                    image.close()
                    isBusy.set(false)
                }
        } else {
            image.close()
        }
    }
}