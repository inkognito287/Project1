package com.example.qrreader.fragment

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.qrreader.Interfaces.UpdateAdapter
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.OnSwipeTouchListener
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request

lateinit var array: ArrayList<DocumentsItem>
  var myAdapter: CustomRecyclerAdapter?=null
  var myAdapterUpdate:UpdateAdapter?=null
class HistoryFragment : Fragment(),CustomRecyclerAdapter.OnItemListener {


    lateinit var binding: FragmentHistoryBinding
    //lateinit var array: ArrayList<DocumentsItem>
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
        binding.historyConstraint.setOnTouchListener(object : OnSwipeTouchListener(activity) {
            override fun onSwipeDown() {
                super.onSwipeDown()
                myAdapter?.notifyDataSetChanged()
            }
        })



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
        binding.progressBar2.visibility = View.VISIBLE
        Thread() {
            val gson = Gson()
            //val kek = gson.fromJson(readToFile(), Response::class.java)
            val zek = readToFile()
            if (zek != "") {
                val kek = gson.fromJson(zek, Response::class.java)
                    array?.clear()
                for (element in kek.documents!!)
                    array?.add(element!!)
                activity?.runOnUiThread() {
                    myAdapter = CustomRecyclerAdapter(array!!, requireContext(),this)
                    myAdapterUpdate = myAdapter
                    binding.recyclerView.adapter = myAdapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
                    myAdapter!!.notifyDataSetChanged()
                    binding.progressBar2.visibility = View.INVISIBLE
                }

              //  deserialize()

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
    fun deserialize() {
        Thread {
        val gson = Gson()

        var result = gson.fromJson(readToFile(), Response::class.java)

        var s = result.documents!!.size-1


            var last = result.documents!![s-1]
            if (last?.status=="no")
                if(imageRequest(
                last?.photo.toString(),
                last?.day!! + " " + last.time!![0].toString() + last.time!![1].toString() + "-" + last.time!![3].toString() + last.time!![4].toString(),
                last.code!!
            )=="true")
                result.documents!![s]!!.status="yes"
                 var resultEnd=gson.toJson(result)
                 writeToFile(resultEnd,context)
        }.start()
    }

    private fun writeToFile(jsonData: String? , context: Context?) {
        try {
            val outputStreamWriter = OutputStreamWriter(
                context?.openFileOutput(
                    "single.json",
                    AppCompatActivity.MODE_PRIVATE
                )
            )
            outputStreamWriter.write(jsonData)

            outputStreamWriter.close()
            println("good")
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }




    fun imageRequest(image: String, name: String, code: String):String? {

            var token = "rerere"
            var sharedPreferencesAddress=activity?.getSharedPreferences("address",Context.MODE_PRIVATE)
            var url=sharedPreferencesAddress?.getString("address","")
            var client = OkHttpClient()
            var requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", image.toString())
                .addFormDataPart("name", name.toString())
                .addFormDataPart("code", code.toString())
                .build();

            var request = Request.Builder()
                .addHeader("token", token)
                .url("$url/Home/image")
                .post(requestBody)
                .build();


            try {
                val response: okhttp3.Response = client.newCall(request).execute()

                return response.body?.string()


                // Do something with the response.
            } catch (e: IOException) {
                Log.d("MyLog", "exception" + e.toString())
                return null
            }



    }
    override fun onItemClick(position: Int) {

        var bundle= Bundle()
        bundle.putInt("position",position)
        var fragment = HistoryItem()
        fragment.arguments=bundle

        var myFragmentTransaction = MyFragmentTransaction(this.requireContext())
        myFragmentTransaction.fragmentTransactionReplace(fragment)
    }

}
