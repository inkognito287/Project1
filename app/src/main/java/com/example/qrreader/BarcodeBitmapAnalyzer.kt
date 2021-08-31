package com.example.qrreader

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
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
import java.lang.Exception

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
                            var correctFormat = false
                            var thisOrderNumber = "-1"
                            if (numberOfPages.contains("http")) {

                                if (numberOfPages.contains("http://static.giprint.ru/doc/")) {
                                    numberOfPages =
                                        numberOfPages.removePrefix("http://static.giprint.ru/doc/")
                                    //0713/OS/1/1
                                    correctFormat = true
                                    thisOrderNumber=numberOfPages.split("/")[0]
                                } else if (numberOfPages.contains("https://static.giprint.ru/doc/")) {
                                    numberOfPages =
                                        numberOfPages.removePrefix("https://static.giprint.ru/doc/")
                                    correctFormat = true
                                    thisOrderNumber=numberOfPages.split("/")[0]
                                } else {
                                    myFunctions.showError("Неизвестный документ")
                                    thisOrderNumber="-1"
                                }


                                try {


                                    allNumberOfPages = numberOfPages.split("/")[3]
                                    numberOfPages = numberOfPages.split("/")[2]
                                } catch (e: Exception) {

                                }
                            }
                            if(thisOrderNumber!=MySingleton.currentOrderNumber && MySingleton.currentOrderNumber!="0"){
                                Log.d("MyLog", thisOrderNumber.toString())
                                Log.d("MyLog", MySingleton.currentOrderNumber.toString())
                                myFunctions.showError("Документ не из этой серии")
                                (context as AppCompatActivity).findViewById<Button>(R.id.button).isClickable =
                                    true

                            }else{


                            if (!correctFormat) {
                                (context as AppCompatActivity).findViewById<Button>(R.id.button).isClickable =
                                    true
                            }
                            if (correctFormat) {
                                var count = 0
                                var flag = false
                                for (x in 0 until MySingleton.arrayList!!.size) {
                                    Log.d("MyLog", information.split("/")[4].split("/")[0])
                                    if (MySingleton.arrayList!![x]!!.numberOfOrderField!!.split("№")[1] == information.split(
                                            "/"
                                        )[4].split("/")[0]
                                    ) {

                                        for (element in MySingleton.arrayList!![x]!!.time)
                                            if (element != null)
                                                count++
                                        if (count == MySingleton.arrayList!![x]!!.day.size) {
                                            myFunctions = Functions(context)
                                            myFunctions.showError("Этот заказ уже укомплектован")
                                            flag = true
                                            (context as AppCompatActivity).findViewById<Button>(R.id.button).isClickable =
                                                true
                                        }

                                        break
                                    }
                                }


                                if (!flag) {


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