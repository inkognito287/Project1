package com.example.qrreader.singletones

import android.graphics.Bitmap
import androidx.databinding.ObservableField
import com.example.qrreader.model.ItemForHistory
import com.google.gson.Gson

object MySingleton {
    lateinit var image: ArrayList<Bitmap?>
    lateinit var title: ArrayList<String?>
    var text: String? = null
    lateinit var status: ArrayList<String?>
    lateinit var day: ArrayList<String?>
    lateinit var time: ArrayList<String?>
    lateinit var countUnsent: ObservableField<String>


    val gson = Gson()
    lateinit var temporaryImage: Bitmap

    var applicationIsActive = true
    var currentPage = 0
    var newSession = true
    var arrayListOfBundlesOfDocuments: ArrayList<ItemForHistory?>? = null
    var pageclick = 0
    lateinit var completedPages: ArrayList<Boolean>
    var numberOfTheChangedItem = 0
    var dontGoOut = 0
    var currentOrderNumber = "0"

    var urlForParsing = ""


}