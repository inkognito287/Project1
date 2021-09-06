package com.example.qrreader.fragment

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrreader.CustomRecyclerAdapter
import com.example.qrreader.Functions
import com.example.qrreader.interfaces.UpdateAdapter
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.databinding.FragmentHistoryBinding
import java.io.OutputStreamWriter


lateinit var myAdapterUpdate: UpdateAdapter

class HistoryFragment : Fragment()
    ,CustomRecyclerAdapter.OnItemListener
{


    lateinit var binding: FragmentHistoryBinding
    lateinit var image: Bitmap
    lateinit var myFunctions: Functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MyLog", "CreateFragment")
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        myFunctions = Functions(requireContext().applicationContext)
        binding = FragmentHistoryBinding.inflate(inflater, container, false)


        Thread(){
        if (myFunctions.readFromFile() == "ERROR") {
            val outputStreamWriter = OutputStreamWriter(
                activity?.openFileOutput(
                    "single.json",
                    AppCompatActivity.MODE_PRIVATE
                )
            )


            outputStreamWriter.close()

        }}.start()


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MyLog", "OnViewCreated")
        //binding.progressBar2.visibility = View.VISIBLE
        var myAdapter = CustomRecyclerAdapter(requireContext(), this)
        binding.recyclerView.adapter = myAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        Thread() {

                activity?.runOnUiThread() {

                   // myAdapter!!.notifyDataSetChanged()
                }



        }.start()

    }


    override fun onResume() {
        super.onResume()

    }


    override fun onItemClick(position: Int) {


        val bundle = Bundle()
       bundle.putInt("position", position)
        val fragment = HistoryItem()
        fragment.arguments = bundle
        val myFragmentTransaction = MyFragmentTransaction(requireContext())
        myFragmentTransaction.fragmentTransactionReplace(fragment)
    }

}
