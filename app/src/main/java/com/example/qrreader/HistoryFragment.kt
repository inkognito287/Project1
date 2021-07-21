package com.example.qrreader

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.Pojo.DocumentsItem
import com.example.qrreader.Pojo.Response
import com.example.qrreader.databinding.FragmentHistoryBinding
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class HistoryFragment : Fragment() {

    //val data="{\"kek\":{\"photo\":\"\",\"code\":\"ES00003885860000000000ASV0201\"}}"
    // Log.d("MyLog","data ="+data)
    var myAdapter: CustomRecyclerAdapter?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MyLog","CreateFragment")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding =FragmentHistoryBinding.inflate(inflater, container,false)
        binding.historyClear.setOnClickListener() {


            try {
                val outputStreamWriter = OutputStreamWriter(
                    activity?.openFileOutput(
                        "single.json",
                        AppCompatActivity.MODE_PRIVATE
                    )
                )
                outputStreamWriter.write("")

                outputStreamWriter.close()

            } catch (e: IOException) {
                Log.e("MyLog", "File write failed: " + e.toString())
            }
            myAdapter?.clearRecyclerView()


        }
        val gson = Gson()
        val kek = gson.fromJson(readToFile(), Response::class.java)
        if (kek!=null) {
            var array = ArrayList<DocumentsItem>()
            for (i in 0 until kek.documents!!.size!!)
                array.add(kek.documents[i]!!)
            Log.d("MyLog", "ReadCheck = " + array)
            myAdapter = CustomRecyclerAdapter(array)
            binding.recyclerView.adapter = myAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

            myAdapter!!.notifyDataSetChanged()
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("MyLog", "OnViewCreated")




            //recyclerView.setAdapter(CustomRecyclerAdapter(array))
           // myAdapter!!.notifyDataSetChanged()



    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        if(myAdapter!=null){

            var array = ArrayList<DocumentsItem>()
            val gson = Gson()
            val kek = gson.fromJson(readToFile(), Response::class.java)
            for (i in 0 until kek.documents!!.size!!)
                array.add(kek.documents[i]!!)
            myAdapter?.updateRecyclerView(array)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun readToFile(): String {
        try {
            val reader = BufferedReader(InputStreamReader(activity?.openFileInput("single.json")))
            val text = reader.readText()
            reader.close()
            return text
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
            return "ERROR"
        }
    }
}
