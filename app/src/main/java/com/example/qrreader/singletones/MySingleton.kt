package com.example.qrreader.singletones

import android.graphics.Bitmap
import com.example.qrreader.Pojo.DocumentsItem
import com.example.qrreader.model.ItemForHistory

object MySingleton {
    lateinit var image: Bitmap
    lateinit var title: String
    lateinit var text: String
    var arrayList: ArrayList<ItemForHistory>?=null

}