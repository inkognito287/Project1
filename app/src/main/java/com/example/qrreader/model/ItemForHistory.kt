package com.example.qrreader.model

import android.graphics.Bitmap

class ItemForHistory(
    var documentFormatField: String,
    var numberOfOrderField: String,
    var stringImage: String?,
    var image: Bitmap,
    var day: String,
    var time: String,
    var status: String,
    var fullInformation: String
) {
}