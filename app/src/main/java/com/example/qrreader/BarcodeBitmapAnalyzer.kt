package com.example.qrreader

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.activities.MainActivity
import com.example.qrreader.fragment.ImageFragment
//import com.example.qrreader.fragment.myAdapter
import com.example.qrreader.fragment.myAdapterUpdate
import com.example.qrreader.singletones.MySingleton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeBitmapAnalyzer(var context: Context) {

    fun scanBarcodes(bitmap: Bitmap, information: String) {

        val rotationDegrees = 0
        var myFunctions: Functions = Functions(context)
        val image = InputImage.fromBitmap(bitmap, 0)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .build()


        val scanner = BarcodeScanning.getClient()


        Thread {
            val result = scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.size == 0) {
                        var myFunctions = Functions(context)
                        myFunctions.showError("Ошибка распознавания qr кода, повторите попытку")

                        (context as AppCompatActivity).findViewById<Button>(R.id.button).isClickable =
                            true
                    }
                    for (barcode in barcodes) {

                        val bounds = barcode.boundingBox
                        val corners = barcode.cornerPoints

                        val rawValue = barcode.rawValue

                        val valueType = barcode.valueType


                        if (information == barcode.rawValue.toString()) {

                            var numberOfPages = information
                            var allNumberOfPages = ""
                            if (numberOfPages.contains("http")) {

                                if (numberOfPages.contains("http://")) {
                                    numberOfPages =
                                        numberOfPages.removePrefix("http://static.giprint.ru/doc/")
                                    //0713/OS/1/1
                                } else if (numberOfPages.contains("https://")) {
                                    numberOfPages =
                                        numberOfPages.removePrefix("https://static.giprint.ru/doc/")
                                }
                                allNumberOfPages = numberOfPages.split("/")[3]
                                numberOfPages = numberOfPages.split("/")[2]

                            }
                            if (numberOfPages.toInt()-1 != MySingleton.pageclick ) {
                                myFunctions.showError("Номер отсканированной страницы не совпадает с ожидаемым ")
                                (context as AppCompatActivity).findViewById<Button>(R.id.button).isClickable =
                                    true
                            } else {
                                val bottomFragment = ImageFragment()
                                val bundle = Bundle()

                                bundle.putString("code", information)
                                bundle.putString("allNumberOfPages", allNumberOfPages)
                                bundle.putString("numberOfPages", numberOfPages)

                                bottomFragment.arguments = bundle
                                (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                                    .replace(R.id.containerBottomSheet, bottomFragment)
                                    .commit()

                                val bottomSheetBehaviour =
                                    BottomSheetBehavior.from(
                                        (context as AppCompatActivity).findViewById(
                                            R.id.containerBottomSheet
                                        )
                                    )

                                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED

                            }
                        }





                        when (valueType) {

                            Barcode.TYPE_WIFI -> {
                                val ssid = barcode.wifi!!.ssid
                                val password = barcode.wifi!!.password
                                val type = barcode.wifi!!.encryptionType
                            }
                            Barcode.TYPE_URL -> {
                                val title = barcode.url!!.title
                                val url = barcode.url!!.url
                            }
                        }
                    }

                }
                .addOnFailureListener {


                    myFunctions.showError("Ошибка распознавания qr кода, повторите попытку")
                    (context as AppCompatActivity).findViewById<Button>(R.id.button).isClickable =
                        true
                }
        }.start()
    }


}