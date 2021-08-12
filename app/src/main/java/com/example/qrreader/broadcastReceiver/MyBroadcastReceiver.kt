package com.example.qrreader.broadcastReceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.Pojo.Response
import com.example.qrreader.fragment.array
import com.example.qrreader.fragment.myAdapter
import com.example.qrreader.Functions
import com.example.qrreader.fragment.myAdapterUpdate
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception


class MyBroadcastReceiver : BroadcastReceiver() {
    lateinit var sharedPreferencesAddress: SharedPreferences
    lateinit var myFunctions: Functions

    @SuppressLint("UnsafeProtectedBroadcastReceiver", "ServiceCast")
    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferencesAddress = context?.getSharedPreferences("address", Context.MODE_PRIVATE)!!
        myFunctions = Functions(context.applicationContext)
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wf = cm.activeNetwork
        val activeNetwork = cm.getNetworkCapabilities(cm.activeNetwork)
            ?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        if (wf != null) {


           startSendImage(context.applicationContext)

        } else
            if (activeNetwork == true) {


                startSendImage(context.applicationContext)

            }
    }


    private fun startSendImage(context: Context){

       var text = myFunctions.readToFile()
        if (text!="")
            try {
                Thread {

                    val gson = Gson()

                    val result = gson.fromJson(text, Response::class.java)



                    for (x in result.documents!!.size - 1 downTo 0) {
                        val last = result.documents[x]
                        if (last?.status == "no")
                            if (myFunctions.imageRequest(
                                    last.photo.toString(),
                                    last.day!! + " " + last.time!![0].toString() + last.time!![1].toString() + "-" + last.time!![3].toString() + last.time!![4].toString(),
                                    last.documentFormatField!!,
                                    sharedPreferencesAddress
                                ) == "true"
                            ) {
                                result.documents[x]?.status = "yes"
                                // array[x]!!.status = "yes"

                            }
                        array.clear()
                        for (x in 0 until result.documents?.size!!)
                            array.add(result.documents[x]!!)

                        myAdapterUpdate = myAdapter
                        myAdapterUpdate?.update()

                        //myAdapter?.update()


                    }

                    val resultEnd = gson.toJson(result)
                    myFunctions.writeToFile(resultEnd)

//                (context.applicationContext as AppCompatActivity).runOnUiThread {
//                    myAdapterUpdate.update()
//
//                }

                }.start()

            }catch (e:Exception){}
    }

}