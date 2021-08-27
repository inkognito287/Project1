package com.example.qrreader.singletones

import android.graphics.Bitmap
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.model.SingleItem
import com.google.gson.Gson

object MySingleton {
    lateinit var image: ArrayList<Bitmap?>
    lateinit var title: ArrayList<String?>
    var text: String? = null
    lateinit var status: ArrayList<String?>
    lateinit var day: ArrayList<String?>
    lateinit var time: ArrayList<String?>
    var cameraScreen: ArrayList<Bitmap> = ArrayList()
    lateinit var countUnsent: ObservableField<String>

    lateinit var singleItem: SingleItem
    var itemForHistory: ItemForHistory? = null
    val gson = Gson()
    lateinit var temporaryImage: Bitmap
    var countActivity: Int = 0
    var applicationIsActive = true
    var currentPage = 0
    var newSession = true
    var arrayList: ArrayList<ItemForHistory?>? = null
    var pageclick = 0
    var completedPages: ArrayList<Boolean> = ArrayList()


}