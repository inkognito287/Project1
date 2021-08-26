package com.example.qrreader.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHistoryItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var myFunctions = Functions(requireContext())

        var arg = arguments?.getInt("position")
//        Log.d("MyLog",MySingleton.arrayList!![arg!!]!!.numberOfOrderField[0]!!.split("№")[1]+"-"+(1).toString())
        //Log.d("MyLog", MySingleton.arrayList!![arg!!].stringImage!!.size.toString())
        item = MySingleton.arrayList!![arg!!]!!

        var imageListener: ImageListener =
            ImageListener { position, imageView -> // You can use Glide or Picasso here
                if (MySingleton.arrayList!![arg]?.numberOfOrderField!![position] != null)
                    imageView.setImageBitmap(
                        BitmapFactory.decodeFile(
                            Environment.getExternalStorageDirectory().absolutePath.toString() + "/" + MySingleton.arrayList!![arg]!!.numberOfOrderField[position]!!.split(
                                "№"
                            )[1] + "page" + (position + 1).toString() + ".png"
                        )
                    )
                else imageView.setImageResource(R.drawable.broken_image)
            }

        var carousel = activity?.findViewById<CarouselView>(R.id.documentImages)
        carousel?.setImageListener(imageListener)
        var listener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (MySingleton.arrayList!![arg]!!.documentFormatField[position] == null) {
                    binding.documentFormat.text = "Не отсканирован"
                    binding.orderNumber.text = ""
                    binding.status.text = ""
                } else {
                    binding.documentFormat.text =
                        MySingleton.arrayList!![arg]!!.documentFormatField[position]
                    binding.orderNumber.text =
                        MySingleton.arrayList!![arg]!!.numberOfOrderField[position]
                    binding.status.text =
                        if (MySingleton.arrayList!![arg]!!.status[position] == "no") "не отправлен" else if(MySingleton.arrayList!![arg]!!.status[position] == "yes") "отправлен"
                    else "не укомплектован"
                }
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }


        }
        carousel?.addOnPageChangeListener(listener)





        carousel?.pageCount = item.status!!.size

        if (MySingleton.arrayList!![arg]!!.documentFormatField[0] != null) {
            binding.documentFormat.text = MySingleton.arrayList!![arg]!!.documentFormatField[0]
            binding.orderNumber.text = MySingleton.arrayList!![arg]!!.numberOfOrderField[0]
            binding.status.text =  if (MySingleton.arrayList!![arg]!!.status[0] == "no") "не отправлен" else if(MySingleton.arrayList!![arg]!!.status[0] == "yes") "отправлен"
            else "не укомплектован"
        } else
            binding.documentFormat.text = "Не отсканирован"
    }

    private fun getImage() {

//           activity?.runOnUiThread {
//                binding.documentImage.setImageBitmap(MySingleton.image)
//                binding.documentFormat.text = MySingleton.text
//                binding.orderNumber.text = MySingleton.title
//                binding.documentImage.scaleType = ImageView.ScaleType.CENTER_CROP
//                binding.status.text = MySingleton.status
//                binding.progressBarHistoryItem.visibility = View.GONE
//            }


    }

}