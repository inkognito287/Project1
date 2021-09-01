package com.example.qrreader.fragment

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.qrreader.Functions
import com.example.qrreader.R
import com.example.qrreader.databinding.FragmentHistoryItemBinding
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.singletones.MySingleton
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener


class HistoryItem : Fragment() {
    lateinit var binding: FragmentHistoryItemBinding
    lateinit var item: ItemForHistory
    var remember = 0
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
        item = MySingleton.arrayList!![numberOfHistoryItem!!]!!

        for (x in 0 until MySingleton.arrayList!![numberOfHistoryItem]!!.documentFormatField.size)
            if (MySingleton.arrayList!![numberOfHistoryItem]!!.documentFormatField[x] != null) {
                remember = x
                break
            }

        val imageListener =
            ImageListener { position, imageView ->
                if (MySingleton.arrayList!![numberOfHistoryItem]?.time!![position] != null)
                    imageView.setImageBitmap(
                        BitmapFactory.decodeFile(
                            Environment.getExternalStorageDirectory().absolutePath.toString() + "/" + MySingleton.arrayList!![numberOfHistoryItem]!!.numberOfOrderField!!.split(
                                "№"
                            )[1] + "page" + (position + 1).toString() + ".png"
                        )
                    )
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
                if (MySingleton.arrayList!![numberOfHistoryItem]!!.documentFormatField[position] == null) {



                    binding.documentFormat.text =
                        MySingleton.arrayList!![numberOfHistoryItem]!!.documentFormatField[remember]!!.split(
                            ","
                        )[0] + ", стр. " + (position + 1) + " из " + MySingleton.arrayList!![numberOfHistoryItem]!!.documentFormatField.size


                    binding.status.text = "Не отсканирован"
                } else {
                    binding.documentFormat.text =
                        MySingleton.arrayList!![numberOfHistoryItem]!!.documentFormatField[position]
                    binding.status.text =
                        if (MySingleton.arrayList!![numberOfHistoryItem]!!.status[0] == "no") "Не отправлен" else if (MySingleton.arrayList!![numberOfHistoryItem]!!.status[0] == "yes") "Отправлен"
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
            MySingleton.arrayList!![numberOfHistoryItem]!!.documentFormatField[remember]!!.split(
                ","
            )[0] + ", стр. " + 1 + " из " + MySingleton.arrayList!![numberOfHistoryItem]!!.documentFormatField.size


        binding.orderNumber.text = MySingleton.arrayList!![numberOfHistoryItem]!!.numberOfOrderField
        carousel?.pageCount = item.status!!.size



        if (MySingleton.arrayList!![numberOfHistoryItem]!!.documentFormatField[0] != null) {

            binding.status.text =
                if (MySingleton.arrayList!![numberOfHistoryItem]!!.status[0] == "yes") "Отправлен"
                else "Не отправлен"

        }

    }


}