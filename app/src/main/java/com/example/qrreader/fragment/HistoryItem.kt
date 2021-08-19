package com.example.qrreader.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.qrreader.R
import com.example.qrreader.databinding.FragmentHistoryItemBinding
import com.example.qrreader.singletones.MySingleton


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

    private fun getImage() {
           activity?.runOnUiThread {
                binding.documentImage.setImageBitmap(MySingleton.image)
                binding.documentFormat.text = MySingleton.text
                binding.orderNumber.text = MySingleton.title
                binding.documentImage.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.status.text = MySingleton.status
                binding.progressBarHistoryItem.visibility = View.GONE
            }


    }

}