package com.example.qrreader.singletones

import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.qrreader.model.ItemForHistory

object MySingleton {
    lateinit var image: Bitmap
    lateinit var title: String
    lateinit var text: String
    lateinit var status: String
    lateinit var cameraScreen: Bitmap
    lateinit var countUnsent: ObservableField<String>
    var countActivity: Int = 0
    var mainActivityExistFlag = true
    var scanActivityExistFlag = true
    var arrayList: ArrayList<ItemForHistory>? = null



}