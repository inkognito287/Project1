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
        binding.imageFragmentSubmit.setOnClickListener() {
            submitImage()
        }
        binding.imageFragmentBack.setOnClickListener() {
            backImage()
        }

        numberOfPages = arguments?.getString("numberOfPages")!!
        allNumberOfPages = arguments?.getString("allNumberOfPages")!!

        if (numberOfPages == allNumberOfPages)
            binding.imageFragmentSubmit.visibility = View.VISIBLE
        else binding.imageFragmentSubmit.visibility = View.GONE

        var pageAdapter = recyclerImageResultAdapter(allNumberOfPages.toInt(), this)
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




        MySingleton.image.add(saveImage)
        MySingleton.title.add(documentFormat)
        MySingleton.text.add(numberOfOrder)
        MySingleton.status.add("no")
        MySingleton.day.add(day)
        MySingleton.time.add(time)



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
        MySingleton.itemForHistory = ItemForHistory(
            MySingleton.title,
            MySingleton.text,
            MySingleton.image,
            MySingleton.day,
            MySingleton.time,
            MySingleton.status,
            MySingleton.text
        )
        MySingleton.arrayList?.add(0, MySingleton.itemForHistory!!)
        myFunctions.saveBitmap(MySingleton.itemForHistory!!.image!!, MySingleton.itemForHistory!!.numberOfOrderField[0])
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
        MySingleton.pageclick = position

        val bottomSheetBehaviour =
            BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        activity?.findViewById<Button>(R.id.button)?.isClickable = true

    }
}