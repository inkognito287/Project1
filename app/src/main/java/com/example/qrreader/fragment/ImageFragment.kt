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
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.graphics.drawable.toBitmap
import com.example.qrreader.BarcodeBitmapAnalyzer
import com.example.qrreader.Functions
import com.example.qrreader.Pojo.Response
import com.example.qrreader.R
import com.example.qrreader.databinding.FragmentImageBinding
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.singletones.MySingleton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class ImageFragment : Fragment() {
    lateinit var imageVew: ImageView

    // lateinit var text: String
    lateinit var saveCode: String
    lateinit var saveImage: Bitmap
    lateinit var binding: FragmentImageBinding
    lateinit var numberOfOrder: String
    lateinit var documentFormat: String
    lateinit var myFunctions: Functions
    lateinit var fullInformation: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater, container, false)
        //binding.imageView7.setImageURI(Uri.parse(arguments?.getString("path")))
        binding.imageFragmentSubmit.setOnClickListener() {
            submitImage()
        }
        binding.imageFragmentBack.setOnClickListener() {
            backImage()
        }





        saveImage = MySingleton.cameraScreen
        saveCode = arguments?.getString("code")!!
        Log.d("MyLog", saveCode)
        imageVew = binding.imageView7
        imageVew.setImageBitmap(saveImage)
        myFunctions = Functions(requireActivity())


        try {
            fullInformation = saveCode
            if (saveCode.contains("http")) {
                if (saveCode.contains("http://")) {
                    saveCode = saveCode.removePrefix("http://")
                } else if (saveCode.contains("https://")) {
                    saveCode = saveCode.removePrefix("https://")
                }
                val parts = saveCode.split("/")
                parts[3]
                numberOfOrder = "Заказ №${parts[2]}"
                documentFormat = "Бланк заказа, стр. ${parts[4]} из ${parts[5]}"
            } else {
                numberOfOrder = "Неизвестный документ "
                documentFormat = ""
            }

        } catch (e: Exception) {
            numberOfOrder = ""
            documentFormat = "Неизвестный документ"
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val barcodeBitmapAnalyzer = BarcodeBitmapAnalyzer(requireActivity())
//        barcodeBitmapAnalyzer.scanBarcodes(binding.imageView7.drawable.toBitmap(),fullInformation)


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun submitImage() {
        binding.imageFragmentSubmit.isClickable = false
        binding.progressBar.visibility = View.VISIBLE
        val date = Date()
        var dateFormatTime = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.US);
        var dateFormatDay = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.US);
        var OUTPUT_PATERN_DAY = SimpleDateFormat("dd MMMM");
        var OUTPUT_PATERN_TIME = SimpleDateFormat("HH:mm");
        var test = dateFormatTime.parse(date.toString())
        var time = OUTPUT_PATERN_TIME.format(test)
        var day = OUTPUT_PATERN_DAY.format(test)
        MySingleton.arrayList!!.add(
            0,
            ItemForHistory(
                documentFormat,
                numberOfOrder,
                null,
                saveImage,
                day,
                time,
                "no",
                fullInformation
            )
        )
        requireActivity().setResult(28)

        requireActivity().finish()
    }

    fun backImage() {
        val bottomSheetBehaviour =
            BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        activity?.findViewById<Button>(R.id.button)?.isClickable = true

        //activity?.findViewById<Button>(R.id.button)?.isClickable=true
    }


}