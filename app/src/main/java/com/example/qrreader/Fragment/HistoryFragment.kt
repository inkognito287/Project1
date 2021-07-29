package com.example.qrreader.Fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrreader.CustomRecyclerAdapter
import com.example.qrreader.Pojo.DocumentsItem
import com.example.qrreader.Pojo.Response
import com.example.qrreader.databinding.FragmentHistoryBinding
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import android.view.MotionEvent
import com.example.qrreader.Interfaces.UpdateAdapter
import com.example.qrreader.OnSwipeTouchListener
import kotlin.concurrent.thread


class HistoryFragment : Fragment(),UpdateAdapter {

    var myAdapter: CustomRecyclerAdapter? = null
    lateinit var binding: FragmentHistoryBinding
    lateinit var array:ArrayList<DocumentsItem>
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
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.historyConstraint.setOnTouchListener(object:  OnSwipeTouchListener(activity){
            override fun onSwipeDown() {
                super.onSwipeDown()
                myAdapter?.notifyDataSetChanged()
            }
        })


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
           // myAdapter?.clearRecyclerView()
            //myAdapter=null
            array.clear()
            myAdapter!!.notifyDataSetChanged()


        }
        if (readToFile() == "ERROR") {
            val outputStreamWriter = OutputStreamWriter(
                activity?.openFileOutput(
                    "single.json",
                    AppCompatActivity.MODE_PRIVATE
                )
            )


            outputStreamWriter.close()

        }


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MyLog", "OnViewCreated")
        binding.progressBar2.visibility=View.VISIBLE
        Thread() {
            val gson = Gson()
            //val kek = gson.fromJson(readToFile(), Response::class.java)
            val zek = readToFile()
            if (zek != "") {
                val kek = gson.fromJson(zek, Response::class.java)
                array = ArrayList<DocumentsItem>()
                for (element in kek.documents!!)
                    array.add(element!!)
                activity?.runOnUiThread() {
                    myAdapter = CustomRecyclerAdapter(array, requireContext())
                    binding.recyclerView.adapter = myAdapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
                    myAdapter!!.notifyDataSetChanged()
                    binding.progressBar2.visibility = View.INVISIBLE
                }


            } else activity?.runOnUiThread() {
                binding.progressBar2.visibility = View.INVISIBLE
            }
        }.start()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun readToFile(): String {

        try {
            val reader =
                BufferedReader(InputStreamReader(activity?.openFileInput("single.json")))
            val text = reader.readText()
            reader.close()
            return text
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
            return "ERROR"
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun update() {

            if (myAdapter != null) {
                val gson = Gson()
                val kek = gson.fromJson(readToFile(), Response::class.java)
                array.clear()
                for (element in kek.documents!!)
                    array.add(element!!)
                activity?.runOnUiThread() {
                    myAdapter?.notifyDataSetChanged()
                }
            }

    }

}
