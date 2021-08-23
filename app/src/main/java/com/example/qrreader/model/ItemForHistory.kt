package com.example.qrreader.model

import android.graphics.Bitmap

class ItemForHistory(
    var documentFormatField: ArrayList<String>,
    var numberOfOrderField: ArrayList<String>,
    var stringImage: ArrayList<String>?,
    var image: ArrayList<Bitmap>,
    var day: ArrayList<String>,
    var time: ArrayList<String>,
    var status: ArrayList<String>,
    var fullInformation: ArrayList<String>
)