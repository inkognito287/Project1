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
//import com.example.qrreader.recyclerAdapter.CustomRecyclerAdapter
import com.example.qrreader.Functions
import com.example.qrreader.R
import com.example.qrreader.databinding.FragmentImageBinding
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.recyclerAdapter.recyclerImageResultAdapter
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
    var mySingleton=MySingleton()
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
        mySingleton.currentPage = numberOfPages.toInt()


        var bottomSheetBehavior =
            BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
        bottomSheetBehavior.isDraggable = false


        saveImage = mySingleton.temporaryImage
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
                else if (parts[3] == "UT")
                    documentFormat = "УПД, стр. ${parts[4]} из ${parts[5]}"
                else if (parts[3] == "CRO")
                    documentFormat = "Приходной ордер, стр. ${parts[4]} из ${parts[5]}"
                else if (parts[3] == "IN")
                    documentFormat = "Счёт-фактура, стр. ${parts[4]} из ${parts[5]}"

            } else {
                numberOfOrder = "Неизвестный документ "
                documentFormat = ""
            }

        } catch (e: Exception) {
            numberOfOrder = ""
            documentFormat = "Неизвестный документ"
        }

        if (mySingleton.completedPages.size == 0) {

            for (i in 0 until allNumberOfPages.toInt()) {

                mySingleton.image.add(i, null)
                mySingleton.title.add(i, null)
                mySingleton.status.add(i, null)
                mySingleton.day.add(i, null)
                mySingleton.time.add(i, null)
            }
        }

        var metCondition = false
        for (x in 0 until mySingleton.arrayListOfBundlesOfDocuments!!.size)

            if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField
                == mySingleton.text
            ) {
                if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size != allNumberOfPages.toInt()) {
                    mySingleton.completedPages = ArrayList()
                    mySingleton.image = ArrayList()
                    mySingleton.title = ArrayList()
                    mySingleton.status = ArrayList()
                    mySingleton.day = ArrayList()
                    mySingleton.time = ArrayList()

                    for (i in 0 until allNumberOfPages.toInt()) {

                        mySingleton.image.add(i, null)
                        mySingleton.title.add(i, null)
                        mySingleton.status.add(i, null)
                        mySingleton.day.add(i, null)
                        mySingleton.time.add(i, null)
                    }
                    mySingleton.image[numberOfPages.toInt() - 1] = saveImage
                    mySingleton.title[numberOfPages.toInt() - 1] = documentFormat
                    mySingleton.text = numberOfOrder
                    mySingleton.status[numberOfPages.toInt() - 1] = "no"
                    mySingleton.day[numberOfPages.toInt() - 1] = day
                    mySingleton.time[numberOfPages.toInt() - 1] = time

                    metCondition = true
                    //////////////////////////////////////////////////
                }
                break

            }
        if (!metCondition) {
            mySingleton.image[numberOfPages.toInt() - 1] = saveImage
            mySingleton.title[numberOfPages.toInt() - 1] = documentFormat
            mySingleton.text = numberOfOrder
            mySingleton.status[numberOfPages.toInt() - 1] = "no"
            mySingleton.day[numberOfPages.toInt() - 1] = day
            mySingleton.time[numberOfPages.toInt() - 1] = time
        }
        if (mySingleton.completedPages.size == 0)
            for (i in 0 until allNumberOfPages.toInt()) {
                mySingleton.completedPages.add(i, false)

            }

        mySingleton.completedPages[numberOfPages.toInt() - 1] = true
        loop@ for (x in 0 until mySingleton.arrayListOfBundlesOfDocuments!!.size)

            if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField
                == mySingleton.text
            ) {
                for (i in 0 until mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField.size)
                    if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i] != null && mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i]!!.split(
                            ","
                        )[0] == documentFormat.split(",")[0]
                    ) {
                        if (allNumberOfPages.toInt() > mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {
                            mySingleton.completedPages[numberOfPages.toInt() - 1] = true

                        } else if (allNumberOfPages.toInt() == mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {
                            for (i in 0 until allNumberOfPages.toInt()) {
                                if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] != null) {
                                    mySingleton.completedPages[i] = true

                                }


                            }
                        }
                        break@loop

                    }
            }

        var count = 0
        for (element in mySingleton.completedPages)
            if (element)
                count++
        if (count == allNumberOfPages.toInt()) {
            //нужно придумать другое решение
            binding.imageFragmentSubmit.text = "Подтвердить"
            binding.imageFragmentSubmit.setOnClickListener() {

                var checkThereIsAlreadyOne = false

         loop@  for (x in 0 until mySingleton.arrayListOfBundlesOfDocuments!!.size)

                    if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField
                        == mySingleton.text
                    ) {
                        for (i in 0 until mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField.size)
                            if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i] != null && mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i]!!.split(
                                    ","
                                )[0] == documentFormat.split(",")[0]
                            ){

                                if (allNumberOfPages.toInt() == mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {
                                    mySingleton.numberOfTheChangedItem = x
                                    for (i in 0 until mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {

                                        if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i] == null)
                                            mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i] =
                                                mySingleton.title[i]
                                        if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField == null)
                                            mySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField =
                                                mySingleton.text

                                        if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] == null)
                                            mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] =
                                                mySingleton.day[i]
                                        if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.time[i] == null)
                                            mySingleton.arrayListOfBundlesOfDocuments!![x]!!.time[i] =
                                                mySingleton.time[i]
                                        if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] == null)
                                            mySingleton.arrayListOfBundlesOfDocuments!![x]!!.status[i] =
                                                mySingleton.status[i]
                                        if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.fullInformation == null)
                                            mySingleton.arrayListOfBundlesOfDocuments!![x]!!.fullInformation =
                                                mySingleton.text
                                    }
                                    for (m in 0 until mySingleton.arrayListOfBundlesOfDocuments!![x]!!.status.size)
                                        mySingleton.arrayListOfBundlesOfDocuments!![x]!!.status[m] =
                                            "no"
                                    checkThereIsAlreadyOne = true
                                    Log.d(
                                        "MyLog",
                                        "currentpage = " + (mySingleton.currentPage - 1).toString()
                                    )
                                    smartSaveBitmap()
                                    mySingleton.newSession = true
                                    break@loop
                                } else {
                                    mySingleton.arrayListOfBundlesOfDocuments!!.removeAt(x)
                                    mySingleton.arrayListOfBundlesOfDocuments!!.add(
                                        x,
                                        ItemForHistory(
                                            mySingleton.title,
                                            mySingleton.text,
                                            mySingleton.day,
                                            mySingleton.time,
                                            mySingleton.status,
                                            mySingleton.text
                                        )
                                    )
                                    Log.d("MyLog", mySingleton.day.toString())
                                    smartSaveBitmap()
                                    checkThereIsAlreadyOne = true
                                    break
                                }
                    }
                    }
                if (!checkThereIsAlreadyOne && mySingleton.newSession) {

                    mySingleton.arrayListOfBundlesOfDocuments?.add(
                        0,
                        ItemForHistory(
                            mySingleton.title,
                            mySingleton.text,
                            mySingleton.day,
                            mySingleton.time,
                            mySingleton.status,
                            mySingleton.text
                        )
                    )
                    smartSaveBitmap()
                    mySingleton.newSession = true
                    mySingleton.image = java.util.ArrayList()
                    mySingleton.title = java.util.ArrayList()
                    mySingleton.text = String()
                    mySingleton.image = java.util.ArrayList()
                    mySingleton.day = java.util.ArrayList()
                    mySingleton.time = java.util.ArrayList()
                    mySingleton.status = java.util.ArrayList()
                } else if (!checkThereIsAlreadyOne && !mySingleton.newSession) {


                    mySingleton.arrayListOfBundlesOfDocuments!![0] = ItemForHistory(
                        mySingleton.title,
                        mySingleton.text,
                        mySingleton.day,
                        mySingleton.time,
                        mySingleton.status,
                        mySingleton.text
                    )
                    smartSaveBitmap()
                }
                submitImage()
                mySingleton.newSession = true
                mySingleton.image = java.util.ArrayList()
                mySingleton.title = java.util.ArrayList()
                mySingleton.text = String()
                mySingleton.image = java.util.ArrayList()
                mySingleton.day = java.util.ArrayList()
                mySingleton.time = java.util.ArrayList()
                mySingleton.status = java.util.ArrayList()
            }

        } else {

            binding.imageFragmentSubmit.text = "Продолжить"
            binding.imageFragmentSubmit.setOnClickListener() {
                mySingleton.dontGoOut = 1
                mySingleton.currentOrderNumber = mySingleton.text!!.split("№")[1]

                val bottomSheetBehaviour =
                    BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
                activity?.findViewById<Button>(R.id.button)?.isClickable = true

                var checkThereIsAlreadyOne = false

                for (x in 0 until mySingleton.arrayListOfBundlesOfDocuments!!.size)

                    if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField
                        == mySingleton.text && mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[0]!!.split(
                            ","
                        )[0] == documentFormat.split(",")[0]
                    ) {
                        if (allNumberOfPages.toInt() == mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {

                            for (i in 0 until mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size) {

                                if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i] == null)
                                    mySingleton.arrayListOfBundlesOfDocuments!![x]!!.documentFormatField[i] =
                                        mySingleton.title[i]
                                if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField == null)
                                    mySingleton.arrayListOfBundlesOfDocuments!![x]!!.numberOfOrderField =
                                        mySingleton.text
                                if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] == null)
                                    mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] =
                                        mySingleton.day[i]
                                if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.time[i] == null)
                                    mySingleton.arrayListOfBundlesOfDocuments!![x]!!.time[i] =
                                        mySingleton.time[i]
                                if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[i] == null)
                                    mySingleton.arrayListOfBundlesOfDocuments!![x]!!.status[i] =
                                        "no"
                                if (mySingleton.arrayListOfBundlesOfDocuments!![x]!!.fullInformation == null)
                                    mySingleton.arrayListOfBundlesOfDocuments!![x]!!.fullInformation =
                                        mySingleton.text
                            }

                            checkThereIsAlreadyOne = true
                            smartSaveBitmap()
                            break

                        } else {
                            mySingleton.arrayListOfBundlesOfDocuments!!.removeAt(x)
                            mySingleton.arrayListOfBundlesOfDocuments!!.add(
                                x, ItemForHistory(
                                    mySingleton.title,
                                    mySingleton.text,
                                    mySingleton.day,
                                    mySingleton.time,
                                    mySingleton.status,
                                    mySingleton.text
                                )
                            )

                            smartSaveBitmap()
                            checkThereIsAlreadyOne = true
                            break
                        }
                    }
                if (mySingleton.newSession && !checkThereIsAlreadyOne) {

                    mySingleton.arrayListOfBundlesOfDocuments?.add(
                        0,
                        ItemForHistory(
                            mySingleton.title,
                            mySingleton.text,
                            mySingleton.day,
                            mySingleton.time,
                            mySingleton.status,
                            mySingleton.text
                        )
                    )
                    smartSaveBitmap()
                    mySingleton.newSession = false
                } else if (!mySingleton.newSession && !checkThereIsAlreadyOne) {

                    mySingleton.arrayListOfBundlesOfDocuments!![0] = ItemForHistory(
                        mySingleton.title,
                        mySingleton.text,
                        mySingleton.day,
                        mySingleton.time,
                        mySingleton.status,
                        mySingleton.text
                    )
                    smartSaveBitmap()
//                        myFunctions.saveBitmap(
//                            MySingleton.image!![MySingleton.currentPage - 1]!!,
//                            MySingleton.arrayList!![0]!!.numberOfOrderField!!,
//                            MySingleton.currentPage
//                        )

                    mySingleton.newSession = false
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

    private fun smartSaveBitmap() {
        myFunctions.saveBitmap(
            mySingleton.image[mySingleton.currentPage - 1]!!,
            mySingleton.arrayListOfBundlesOfDocuments!![0]!!.numberOfOrderField!!,
            mySingleton.currentPage,
            mySingleton.status.size,
            mySingleton.arrayListOfBundlesOfDocuments!![0]!!.documentFormatField[mySingleton.currentPage - 1]!!

        )
    }

    private fun submitImage() {
        binding.imageFragmentSubmit.isClickable = false
        binding.progressBar.visibility = View.VISIBLE

        mySingleton.completedPages.clear()
        requireActivity().setResult(3)
        requireActivity().finish()
    }


    override fun onItemClick(position: Int) {

    }

}