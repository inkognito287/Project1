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
            //backImage()
            activity?.onBackPressed()
        }

        numberOfPages = arguments?.getString("numberOfPages")!!
        allNumberOfPages = arguments?.getString("allNumberOfPages")!!
        MySingleton.currentPage = numberOfPages.toInt()


        var bottomSheetBehavior =
            BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
        bottomSheetBehavior.isDraggable = false


        saveImage = MySingleton.temporaryImage
        Log.d("MyLog", saveImage.toString())
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


                numberOfOrder = "Заказ №${parts[2]}"
                if (parts[3] == "OS")
                    documentFormat = "Бланк заказа, стр. ${parts[4]} из ${parts[5]}"
                else if (parts[3] == "utd")
                    documentFormat = "УПД, стр. ${parts[4]} из ${parts[5]}"
                else if (parts[3] == "inv")
                    documentFormat = "Счёт-фактура, стр. ${parts[4]} из ${parts[5]}"
            } else {
                numberOfOrder = "Неизвестный документ "
                documentFormat = ""
            }

        } catch (e: Exception) {
            numberOfOrder = ""
            documentFormat = "Неизвестный документ"
        }

        if (MySingleton.completedPages.size == 0) {

            for (i in 0 until allNumberOfPages.toInt()) {

                MySingleton.image.add(i, null)
                MySingleton.title.add(i, null)
                MySingleton.status.add(i, null)
                MySingleton.day.add(i, null)
                MySingleton.time.add(i, null)
            }
        }

        var metCondition = false
        for (x in 0 until MySingleton.arrayListOfBundlesOfDocuments!!.size)

            if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField
                == MySingleton.text
            ) {
                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size != allNumberOfPages.toInt()) {
                    MySingleton.completedPages = ArrayList()
                    MySingleton.image = ArrayList()
                    MySingleton.title = ArrayList()
                    MySingleton.status = ArrayList()
                    MySingleton.day = ArrayList()
                    MySingleton.time = ArrayList()

                    for (i in 0 until allNumberOfPages.toInt()) {

                        MySingleton.image.add(i, null)
                        MySingleton.title.add(i, null)
                        MySingleton.status.add(i, null)
                        MySingleton.day.add(i, null)
                        MySingleton.time.add(i, null)
                    }
                    MySingleton.image[numberOfPages.toInt() - 1] = saveImage
                    MySingleton.title[numberOfPages.toInt() - 1] = documentFormat
                    MySingleton.text = numberOfOrder
                    MySingleton.status[numberOfPages.toInt() - 1] = "no"
                    MySingleton.day[numberOfPages.toInt() - 1] = day
                    MySingleton.time[numberOfPages.toInt() - 1] = time

                    metCondition = true
                    //////////////////////////////////////////////////
                     }
                break

            }
        if (!metCondition){
            MySingleton.image[numberOfPages.toInt() - 1] = saveImage
            MySingleton.title[numberOfPages.toInt() - 1] = documentFormat
            MySingleton.text = numberOfOrder
            MySingleton.status[numberOfPages.toInt() - 1] = "no"
            MySingleton.day[numberOfPages.toInt() - 1] = day
            MySingleton.time[numberOfPages.toInt() - 1] = time
        }
        if (MySingleton.completedPages.size == 0)
            for (i in 0 until allNumberOfPages.toInt()) {
                MySingleton.completedPages.add(i, false)

            }

        MySingleton.completedPages[numberOfPages.toInt() - 1] = true
        for (x in 0 until MySingleton.arrayListOfBundlesOfDocuments!!.size)

            if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField
                == MySingleton.text
            ) {

                if (allNumberOfPages.toInt() > MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {
                    MySingleton.completedPages[numberOfPages.toInt() - 1] = true

                } else if (allNumberOfPages.toInt() == MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size){
                    for (i in 0 until allNumberOfPages.toInt()) {
                        if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] != null) {
                            MySingleton.completedPages[i] = true

                        }


                }}
                break

            }

        var count = 0
        for (element in MySingleton.completedPages)
            if (element)
                count++
        if (count == allNumberOfPages.toInt()) {
            //нужно придумать другое решение
            binding.imageFragmentSubmit.text = "Подтвердить"
            binding.imageFragmentSubmit.setOnClickListener() {

                var checkThereIsAlreadyOne = false

                for (x in 0 until MySingleton.arrayListOfBundlesOfDocuments!!.size)

                    if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField
                        == MySingleton.text
                    ) {
                        if (allNumberOfPages.toInt() == MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {
                            MySingleton.numberOfTheChangedItem = x
                            for (i in 0 until MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {

                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i] == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i] =
                                        MySingleton.title[i]
                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField =
                                        MySingleton.text

                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] = MySingleton.day[i]
                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.time[i] == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.time[i] = MySingleton.time[i]
                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.status[i] =
                                        MySingleton.status[i]
                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.fullInformation == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.fullInformation =
                                        MySingleton.text
                            }
                            for (m in 0 until MySingleton.arrayListOfBundlesOfDocuments!![x]!!.status.size)
                                MySingleton.arrayListOfBundlesOfDocuments!![x]!!.status[m] = "no"
                            checkThereIsAlreadyOne = true
                            Log.d(
                                "MyLog",
                                "currentpage = " + (MySingleton.currentPage - 1).toString()
                            )
                            smartSaveBitmap()
                            MySingleton.newSession = true
                            break
                        } else {
                            MySingleton.arrayListOfBundlesOfDocuments!!.removeAt(x)
                            MySingleton.arrayListOfBundlesOfDocuments!!.add(
                                x,
                                ItemForHistory(
                                    MySingleton.title,
                                    MySingleton.text,
                                    MySingleton.day,
                                    MySingleton.time,
                                    MySingleton.status,
                                    MySingleton.text
                                )
                            )
                            Log.d("MyLog", MySingleton.day.toString())
                            smartSaveBitmap()
                            checkThereIsAlreadyOne = true
                            break
                        }
                    }
                if (!checkThereIsAlreadyOne&&MySingleton.newSession) {

                        MySingleton.arrayListOfBundlesOfDocuments?.add(
                            0,
                            ItemForHistory(
                                MySingleton.title,
                                MySingleton.text,
                                MySingleton.day,
                                MySingleton.time,
                                MySingleton.status,
                                MySingleton.text
                            )
                        )
                        smartSaveBitmap()
                    MySingleton.newSession = true
                    MySingleton.image = java.util.ArrayList()
                    MySingleton.title = java.util.ArrayList()
                    MySingleton.text = String()
                    MySingleton.image = java.util.ArrayList()
                    MySingleton.day = java.util.ArrayList()
                    MySingleton.time = java.util.ArrayList()
                    MySingleton.status = java.util.ArrayList()
                } else if(!checkThereIsAlreadyOne&&!MySingleton.newSession){




                        MySingleton.arrayListOfBundlesOfDocuments!![0] = ItemForHistory(
                            MySingleton.title,
                            MySingleton.text,
                            MySingleton.day,
                            MySingleton.time,
                            MySingleton.status,
                            MySingleton.text
                        )
                          smartSaveBitmap()
                }
                submitImage()
                MySingleton.newSession = true
                MySingleton.image = java.util.ArrayList()
                MySingleton.title = java.util.ArrayList()
                MySingleton.text = String()
                MySingleton.image = java.util.ArrayList()
                MySingleton.day = java.util.ArrayList()
                MySingleton.time = java.util.ArrayList()
                MySingleton.status = java.util.ArrayList()
            }

        } else {

            binding.imageFragmentSubmit.text = "Продолжить"
            binding.imageFragmentSubmit.setOnClickListener() {
                MySingleton.dontGoOut = 1
                MySingleton.currentOrderNumber = MySingleton.text!!.split("№")[1]

                val bottomSheetBehaviour =
                    BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                activity?.findViewById<Button>(R.id.button)?.isClickable = true

                var checkThereIsAlreadyOne = false

                for (x in 0 until MySingleton.arrayListOfBundlesOfDocuments!!.size)

                    if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField
                        == MySingleton.text
                    ) {
                        if (allNumberOfPages.toInt() == MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {

                            for (i in 0 until MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {

                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i] == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i] =
                                        MySingleton.title[i]
                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField =
                                        MySingleton.text
                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] = MySingleton.day[i]
                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.time[i] == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.time[i] = MySingleton.time[i]
                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.status[i] = "no"
                                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.fullInformation == null)
                                    MySingleton.arrayListOfBundlesOfDocuments!![x]!!.fullInformation =
                                        MySingleton.text
                            }

                            checkThereIsAlreadyOne = true
                            smartSaveBitmap()
                            break

                        } else {
                            MySingleton.arrayListOfBundlesOfDocuments!!.removeAt(x)
                            MySingleton.arrayListOfBundlesOfDocuments!!.add(
                                x, ItemForHistory(
                                    MySingleton.title,
                                    MySingleton.text,
                                    MySingleton.day,
                                    MySingleton.time,
                                    MySingleton.status,
                                    MySingleton.text
                                )
                            )

                            smartSaveBitmap()
                            checkThereIsAlreadyOne = true
                            break
                        }
                    }
                if (MySingleton.newSession&&!checkThereIsAlreadyOne) {

                        MySingleton.arrayListOfBundlesOfDocuments?.add(
                            0,
                            ItemForHistory(
                                MySingleton.title,
                                MySingleton.text,
                                MySingleton.day,
                                MySingleton.time,
                                MySingleton.status,
                                MySingleton.text
                            )
                        )
                        smartSaveBitmap()
                    MySingleton.newSession = false
                } else if (!MySingleton.newSession&&!checkThereIsAlreadyOne){

                        MySingleton.arrayListOfBundlesOfDocuments!![0] = ItemForHistory(
                            MySingleton.title,
                            MySingleton.text,
                            MySingleton.day,
                            MySingleton.time,
                            MySingleton.status,
                            MySingleton.text
                        )
                        smartSaveBitmap()
//                        myFunctions.saveBitmap(
//                            MySingleton.image!![MySingleton.currentPage - 1]!!,
//                            MySingleton.arrayList!![0]!!.numberOfOrderField!!,
//                            MySingleton.currentPage
//                        )

                        MySingleton.newSession = false
                }
            }
        }


        val pageAdapter =
            recyclerImageResultAdapter(allNumberOfPages.toInt(), numberOfPages.toInt(), this)
        binding.pageNumbers.adapter = pageAdapter
        binding.pageNumbers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        Log.d("MyLog", saveCode)
        imageVew = binding.imageView7
        imageVew.setImageBitmap(saveImage)
        myFunctions = Functions(requireActivity())
        return binding.root
    }

    private  fun smartSaveBitmap(){
        myFunctions.saveBitmap(
            MySingleton.image!![MySingleton.currentPage - 1]!!,
            MySingleton.arrayListOfBundlesOfDocuments!![0]!!.numberOfOrderField!!,
            MySingleton.currentPage
        )
    }
    private fun submitImage() {
        binding.imageFragmentSubmit.isClickable = false
        binding.progressBar.visibility = View.VISIBLE

        MySingleton.completedPages.clear()
        requireActivity().setResult(3)
        requireActivity().finish()
    }


    override fun onItemClick(position: Int) {

    }

}