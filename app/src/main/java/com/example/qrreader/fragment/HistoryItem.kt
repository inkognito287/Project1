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

        binding.progressBarHistoryItem.visibility = View.VISIBLE
        var arg = arguments?.getInt("position")
        Log.d("MyLog",MySingleton.arrayList!![arg!!].numberOfOrderField[0].split("№")[1]+"-"+(1).toString())
        //Log.d("MyLog", MySingleton.arrayList!![arg!!].stringImage!!.size.toString())
         item = MySingleton.arrayList!![arg!!]

        var imageListener: ImageListener = object : ImageListener {
            override fun setImageForPosition(position: Int, imageView: ImageView) {
                // You can use Glide or Picasso here

               imageView.setImageBitmap(BitmapFactory.decodeFile( Environment.getExternalStorageDirectory().absolutePath.toString()+"/"+MySingleton.arrayList!![arg].numberOfOrderField[0].split("№")[1]+"page"+(position+1).toString()+".png"))
            }

        }
        var carousel = activity?.findViewById<CarouselView>(R.id.documentImages)
        carousel?.setImageListener(imageListener)




        carousel?.pageCount = item.status!!.size


        binding.documentFormat.text = MySingleton.arrayList!![requireArguments().getInt("position",44)].documentFormatField[0]
        binding.orderNumber.text = MySingleton.arrayList!![requireArguments().getInt("position",44)].numberOfOrderField[0]
        binding.status.text = MySingleton.arrayList!![requireArguments().getInt("position",44)].status[0]

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