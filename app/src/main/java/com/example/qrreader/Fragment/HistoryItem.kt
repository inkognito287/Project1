package com.example.qrreader.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.qrreader.Pojo.DocumentsItem
import com.example.qrreader.Pojo.Response
import com.example.qrreader.databinding.FragmentHistoryItemBinding
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList


class HistoryItem : Fragment() {
   lateinit var binding:FragmentHistoryItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHistoryItemBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            getImage(readToFile(), requireArguments().getInt("position"))

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readToFile(): String {

        try {
            val reader =
                BufferedReader(InputStreamReader(activity?.openFileInput("single.json")))
            val text = reader.readText()
            reader.close()
            return text
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
            return "ERROR"
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getImage(json:String,position:Int){
        val array = ArrayList<DocumentsItem>()
        val gson = Gson()
        val kek = gson.fromJson(readToFile(), Response::class.java)
        for (element in kek.documents!!)
            array.add(element!!)
       binding.documentImage.setImageBitmap((getBitmapFromString(array[position].photo!!)!!))
       binding.documentFormat.text=array[position].date
       binding.orderNumber.text=array[position].code
        binding.documentImage.scaleType=ImageView.ScaleType.CENTER_CROP


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBitmapFromString(stringPicture: String): Bitmap? {
        val decodedString: ByteArray = Base64.getDecoder().decode(stringPicture)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

}