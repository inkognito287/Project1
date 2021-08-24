package com.example.qrreader.singletones

import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.model.SingleItem
import com.google.gson.Gson

object MySingleton {
    var image: ArrayList<Bitmap> = ArrayList()
    var title: ArrayList<String> = ArrayList()
    var text: ArrayList<String> = ArrayList()
    var status: ArrayList<String> = ArrayList()
    var day: ArrayList<String> = ArrayList()
    var time: ArrayList<String> = ArrayList()
    var cameraScreen: ArrayList<Bitmap> = ArrayList()
    lateinit var countUnsent: ObservableField<String>
    lateinit var singleItem: SingleItem
    var itemForHistory: ItemForHistory? =null
    val gson = Gson()
    lateinit var temporaryImage: Bitmap
    var countActivity: Int = 0
    var mainActivityExistFlag = true
    var scanActivityExistFlag = true
    var arrayList: ArrayList<ItemForHistory>? = null
    var pageclick = 0


}