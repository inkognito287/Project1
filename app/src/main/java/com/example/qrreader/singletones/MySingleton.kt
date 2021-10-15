package com.example.qrreader.singletones

import android.graphics.Bitmap
import androidx.databinding.ObservableField
import com.example.qrreader.model.ItemForHistory
import com.google.gson.Gson

class MySingleton {


    //var urlForParsing = ""



    var urlForParsing = ""
        get() = field
        set(value) {
            field = value
        }
    var currentOrderNumber = "0"
        get() = field
        set(value) {
            field = value
        }
    lateinit var image: ArrayList<Bitmap?>
    lateinit var title: ArrayList<String?>
    var text: String? = null
        get() = field
        set(value) {
            field = value
        }
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
        get() = field
        set(value) {
            field = value
        }
    var pageclick = 0
        get() = field
        set(value) {
            field = value
        }
    lateinit var completedPages: ArrayList<Boolean>
        get() = field
        set(value) {
            field = value
        }
    var numberOfTheChangedItem = 0
    var dontGoOut = 0
}