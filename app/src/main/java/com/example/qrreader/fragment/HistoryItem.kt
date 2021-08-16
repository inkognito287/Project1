package com.example.qrreader.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
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
import com.example.qrreader.R
import com.example.qrreader.databinding.FragmentHistoryItemBinding
import com.example.qrreader.singletones.MySingleton
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList


class HistoryItem : Fragment() {
    lateinit var binding: FragmentHistoryItemBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHistoryItemBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBarHistoryItem.visibility = View.VISIBLE
        Thread() {
            getImage()
        }.start()
    }


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


    fun getImage() {
           activity?.runOnUiThread {
                binding.documentImage.setImageBitmap(MySingleton.image)
                binding.documentFormat.text = MySingleton.text
                binding.orderNumber.text = MySingleton.title
                binding.documentImage.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.status.text = MySingleton.status
               if (MySingleton.status=="Статус: отправлен")
                binding.status.setBackgroundResource(R.color.sent)
               else
                   binding.status.setBackgroundResource(R.color.waiting)
                binding.progressBarHistoryItem.visibility = View.GONE
            }


    }


    private fun getBitmapFromString(stringPicture: String): Bitmap? {
        val decodedString: ByteArray = android.util.Base64.decode(stringPicture,Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

}