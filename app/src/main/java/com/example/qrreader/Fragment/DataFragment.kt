package com.example.qrreader.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.R
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
    ): View? {

        binding = FragmentDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreference = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        if (sharedPreference.contains("key") && sharedPreference.contains("address")) {
            binding.editTextTextKey.isEnabled = false
            binding.editTextTextAddress.isEnabled = false

        } else {
            binding.enterDataButton.setOnClickListener() {
                sharedPreference.edit().putString("key", binding.editTextTextKey.text.toString())
                    .putString(
                        "address",
                        binding.editTextTextAddress.text.toString()

                    ).apply()
                binding.button3.isEnabled = true
                requireActivity().findViewById<Button>(R.id.buttonHistory).isEnabled = true
                requireActivity().findViewById<Button>(R.id.button).isEnabled = true
                requireActivity().findViewById<Button>(R.id.buttonSetting).isEnabled = true



                myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())
            }

            binding.button3.isEnabled = false
            requireActivity().findViewById<Button>(R.id.buttonHistory).isEnabled = false
            requireActivity().findViewById<Button>(R.id.button).isEnabled = false
            requireActivity().findViewById<Button>(R.id.buttonSetting).isEnabled = false

            binding.editTextTextKey.isEnabled = true
            binding.editTextTextAddress.isEnabled = true

        }
    }


}