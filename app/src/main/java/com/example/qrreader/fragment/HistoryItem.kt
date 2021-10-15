package com.example.qrreader.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.qrreader.R
import com.example.qrreader.databinding.FragmentHistoryItemBinding
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.singletones.MySingleton

import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import java.io.File


class HistoryItem : Fragment() {
    lateinit var binding: FragmentHistoryItemBinding
    lateinit var item: ItemForHistory
    var remember = 0
    var mySingleton= MySingleton()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHistoryItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numberOfHistoryItem = arguments?.getInt("position")
        item = mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem!!]!!

        for (x in 0 until mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField.size)
            if (mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField[x] != null) {
                remember = x
                break
            }

        val imageListener =
            ImageListener { position, imageView ->
                if (mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]?.time!![position] != null) {

                    var file = File(
                        Environment.getExternalStorageDirectory().absolutePath.toString() + "/" +
                                mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField[position]!!.split(",")[0].replace(" ","-")+
                                "-<"+
                                mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.numberOfOrderField!!.split("№")[1] +
                                ">-<"+
                                (position + 1).toString() +
                                ">-<"+
                                mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField.size+
                                ">"+
                                ".jpg"
                    )
                    Log.d("MyLog","Путь-"+file.path)
                    //Бланк-заказа-<номер заказа>-<номер страницы>-<всего страниц>.jpg
                    var uri = Uri.fromFile(file)
                    imageView.setImageURI(uri)
                }


                else imageView.setImageResource(R.drawable.broken_image)
            }

        val carousel = activity?.findViewById<CarouselView>(R.id.documentImages)
        carousel?.setImageListener(imageListener)

        val listener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField[position] == null) {



                    binding.documentFormat.text =
                        mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField[remember]!!.split(
                            ","
                        )[0] + ", стр. " + (position + 1) + " из " + mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField.size


                    binding.status.text = "Не отсканирован"
                } else {
                    binding.documentFormat.text =
                        mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField[position]
                    binding.status.text =
                        if (mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.status[0] == "no") "Не отправлен" else if (mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.status[0] == "yes") "Отправлен"
                        else "Не отправлен"
                }
            }
            override fun onPageSelected(position: Int) {
            }
            override fun onPageScrollStateChanged(state: Int) {
            }


        }
        carousel?.addOnPageChangeListener(listener)

        binding.documentFormat.text =
            mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField[remember]!!.split(
                ","
            )[0] + ", стр. " + 1 + " из " + mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField.size


        binding.orderNumber.text = mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.numberOfOrderField
        carousel?.pageCount = item.status!!.size



        if (mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.documentFormatField[0] != null) {

            binding.status.text =
                if (mySingleton.arrayListOfBundlesOfDocuments!![numberOfHistoryItem]!!.status[0] == "yes") "Отправлен"
                else "Не отправлен"

        }

    }


}