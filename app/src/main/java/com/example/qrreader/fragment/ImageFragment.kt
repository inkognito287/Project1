package com.example.qrreader.fragment

import android.graphics.Bitmap
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
import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.qrreader.CustomRecyclerAdapter
import com.example.qrreader.Functions
import com.example.qrreader.R
import com.example.qrreader.databinding.FragmentImageBinding
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.model.SingleItem
import com.example.qrreader.recyclerImageResultAdapter
import com.example.qrreader.singletones.MySingleton
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ImageFragment : Fragment(), recyclerImageResultAdapter.OnItemListener {
    lateinit var imageVew: ImageView
    lateinit var saveCode: String
    lateinit var saveImage: Bitmap
    lateinit var binding: FragmentImageBinding
    lateinit var numberOfOrder: String
    lateinit var documentFormat: String
    lateinit var myFunctions: Functions
    lateinit var fullInformation: String
    lateinit var allNumberOfPages: String
    lateinit var numberOfPages: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentImageBinding.inflate(inflater, container, false)

        binding.imageFragmentBack.setOnClickListener() {
            backImage()
        }

        numberOfPages = arguments?.getString("numberOfPages")!!
        allNumberOfPages = arguments?.getString("allNumberOfPages")!!
        if (MySingleton.completedPages.size == 0)
            for (i in 0 until allNumberOfPages.toInt()) {
                MySingleton.completedPages.add(i, false)
                MySingleton.image.add(i, null)
                MySingleton.title.add(i, null)
                MySingleton.text.add(i, null)
                MySingleton.status.add(i, null)
                MySingleton.day.add(i, null)
                MySingleton.time.add(i, null)
            }
        MySingleton.completedPages[numberOfPages.toInt() - 1] = true

        if (numberOfPages == allNumberOfPages) {
            binding.imageFragmentSubmit.text = "Подтвердить"
            binding.imageFragmentSubmit.setOnClickListener() {
                submitImage()
            }
        } else {
            binding.imageFragmentSubmit.setOnClickListener() {
                //MySingleton.pageclick = position

                val bottomSheetBehaviour =
                    BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                activity?.findViewById<Button>(R.id.button)?.isClickable = true
            }
            binding.imageFragmentSubmit.text = "Продолжить"
        }

        var pageAdapter =
            recyclerImageResultAdapter(allNumberOfPages.toInt(), numberOfPages.toInt(), this)
        binding.pageNumbers.adapter = pageAdapter
        binding.pageNumbers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        var bottomSheetBehavior =
            BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
        bottomSheetBehavior.isDraggable = false


        saveImage = MySingleton.temporaryImage
        saveCode = arguments?.getString("code")!!

        val date = Date()
        val dateFormatTime = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.US);
        val OUTPUT_PATERN_DAY = SimpleDateFormat("dd MMMM");
        val OUTPUT_PATERN_TIME = SimpleDateFormat("HH:mm");
        val test = dateFormatTime.parse(date.toString())
        val time = OUTPUT_PATERN_TIME.format(test)
        val day = OUTPUT_PATERN_DAY.format(test)

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


//      if (MySingleton.image.size>= numberOfPages.toInt() - 1) {
        MySingleton.image[numberOfPages.toInt() - 1] = saveImage
        MySingleton.title[numberOfPages.toInt() - 1] = documentFormat
        MySingleton.text[numberOfPages.toInt() - 1] = numberOfOrder
        MySingleton.status[numberOfPages.toInt() - 1] = "no"
        MySingleton.day[numberOfPages.toInt() - 1] = day
        MySingleton.time[numberOfPages.toInt() - 1] = time
//
//        } else {
//            MySingleton.image.add(numberOfPages.toInt(),saveImage)
//            MySingleton.title.add(numberOfPages.toInt(),documentFormat)
//            MySingleton.text.add(numberOfPages.toInt(),numberOfOrder)
//            MySingleton.status.add(numberOfPages.toInt(),"no")
//            MySingleton.day.add(numberOfPages.toInt(),day)
//            MySingleton.time.add(numberOfPages.toInt(),time)
        //}


        Log.d("MyLog", saveCode)
        imageVew = binding.imageView7
        imageVew.setImageBitmap(saveImage)
        myFunctions = Functions(requireActivity())




        return binding.root
    }


    private fun submitImage() {
        binding.imageFragmentSubmit.isClickable = false
        binding.progressBar.visibility = View.VISIBLE


//        MySingleton.singleItem = SingleItem(
//            documentFormat,
//            numberOfOrder,
//            null,
//            saveImage,
//            day,
//            time,
//            "no",
//            fullInformation
//        )
//
//
//        val itemForHistory=ItemForHistory(
//        MySingleton.title,
//        MySingleton.text,
//        MySingleton.image,
//        MySingleton.day,
//        MySingleton.time,
//        MySingleton.status,
//        MySingleton.text
//        )
        // MySingleton.itemForHistory = itemForHistory
        // var zpp = itemForHistory


        MySingleton.arrayList?.add(
            0, ItemForHistory(
                MySingleton.title,
                MySingleton.text,
                MySingleton.image,
                MySingleton.day,
                MySingleton.time,
                MySingleton.status,
                MySingleton.text
            )
        )


        requireActivity().setResult(28)
        requireActivity().finish()
    }

    private fun backImage() {
        val bottomSheetBehaviour =
            BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        activity?.findViewById<Button>(R.id.button)?.isClickable = true
    }

    override fun onItemClick(position: Int) {
//        if (position==numberOfPages.toInt()) {
//            MySingleton.pageclick = position
//
//            val bottomSheetBehaviour =
//                BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
//            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
//            activity?.findViewById<Button>(R.id.button)?.isClickable = true
//        }
    }
}