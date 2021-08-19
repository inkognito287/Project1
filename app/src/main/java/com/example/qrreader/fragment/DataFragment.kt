package com.example.qrreader.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.databinding.FragmentDataBinding


class DataFragment : Fragment() {

    lateinit var binding: FragmentDataBinding
    lateinit var myFragmentTransaction: MyFragmentTransaction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myFragmentTransaction = MyFragmentTransaction(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreference = requireActivity().getSharedPreferences("address", Context.MODE_PRIVATE)

            binding.editTextTextKey.isEnabled = false
            binding.editTextTextKey.setText(sharedPreference.getString("key", ""))
            binding.editTextTextAddress.isEnabled = false
            binding.editTextTextAddress.setText(sharedPreference.getString("address", ""))



}
}