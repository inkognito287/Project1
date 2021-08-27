package com.example.qrreader.model

import android.graphics.Bitmap

class ItemForHistory(
    var documentFormatField: ArrayList<String?>,
    var numberOfOrderField: String?,
    val image: ArrayList<Bitmap?>?,
    val day: ArrayList<String?>,
    val time: ArrayList<String?>,
    val status: ArrayList<String?>,
    var fullInformation: String?
)