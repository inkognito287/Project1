package com.example.qrreader.singletones

import android.graphics.Bitmap
import com.example.qrreader.Pojo.DocumentsItem
import com.example.qrreader.model.ItemForHistory

object MySingleton {
    lateinit var image: Bitmap
    lateinit var title: String
    lateinit var text: String
    lateinit var status: String
    lateinit var cameraScreen: Bitmap
    var countActivity: Int = 0
    var flag=true
    var flag2=true

    var arrayList: ArrayList<ItemForHistory>?=null


}