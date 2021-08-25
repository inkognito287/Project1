package com.example.qrreader.model

import android.graphics.Bitmap

class ItemForHistory(
    val documentFormatField: ArrayList<String?>,
    val numberOfOrderField: ArrayList<String?>,
    val image: ArrayList<Bitmap?>?,
    val day: ArrayList<String?>,
    val time: ArrayList<String?>,
    val status: ArrayList<String?>,
    val fullInformation: ArrayList<String?>
)