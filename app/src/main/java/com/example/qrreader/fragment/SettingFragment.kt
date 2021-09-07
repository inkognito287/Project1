package com.example.qrreader.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.qrreader.activities.MainActivity
import com.example.qrreader.model.SettingData

import com.example.qrreader.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    lateinit var binding:FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.settingItems = SettingData().getSettingItems()
        binding.activity = activity as MainActivity

    }
}