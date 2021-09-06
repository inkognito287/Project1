package com.example.qrreader.broadcastReceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.Functions
import com.example.qrreader.R

//import com.example.qrreader.fragment.myAdapter
import com.example.qrreader.singletones.MySingleton


class MyBroadcastReceiver : BroadcastReceiver() {
    lateinit var sharedPreferencesAddress: SharedPreferences
    lateinit var sharedPreferencesUser: SharedPreferences
    lateinit var myFunctions: Functions

    @SuppressLint("UnsafeProtectedBroadcastReceiver", "ServiceCast")
    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferencesAddress = context?.getSharedPreferences("address", Context.MODE_PRIVATE)!!
        sharedPreferencesUser = context.getSharedPreferences("user", Context.MODE_PRIVATE)!!
        myFunctions = Functions(context)
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wf = cm.activeNetwork
        val activeNetwork = cm.getNetworkCapabilities(cm.activeNetwork)
            ?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        if (wf != null) {


            startSendImage(context)

        } else
            if (activeNetwork == true) {


                startSendImage(context)

            }
    }


    private fun startSendImage(context: Context) {


        Thread {


            for (y in MySingleton.arrayListOfBundlesOfDocuments?.size!! - 1 downTo 0) {
                val document = MySingleton.arrayListOfBundlesOfDocuments!![y]

                var count = 0
                for (numberOfStatusField in 0 until document!!.status.size)
                    if (document.status[numberOfStatusField] == null)
                        count++
                if (count == 0)
                    for (x in 0 until document.status.size)
                        if (document.status[x] == "no")
                            if (myFunctions.imageRequest(
                                    myFunctions.getStringFromBitmap(
                                        BitmapFactory.decodeFile(
                                            Environment.getExternalStorageDirectory().absolutePath.toString() + "/" + MySingleton.arrayListOfBundlesOfDocuments!![y]!!.numberOfOrderField!!.split(
                                                "№"
                                            )[1] + "page" + (x + 1).toString() + ".png"
                                        )
                                    )!!,
                                    document.day[x]!! + " " + document.time!![x]!![0].toString() + document.time!![x]!![1].toString() + "-" + document.time!![x]!![3].toString() + document.time!![x]!![4].toString(),
                                    document.fullInformation!!,
                                    sharedPreferencesAddress,
                                    sharedPreferencesUser
                                ) == "true"
                            ) {
                                document.status[x] = "yes"
                                if (x == 0) {
                                    MySingleton.countUnsent.set(
                                        (MySingleton.countUnsent.get()!!.toInt() - 1).toString()
                                    )
                                    try {
                                        (context as AppCompatActivity).findViewById<RecyclerView>(R.id.recycler_view).adapter?.notifyDataSetChanged()

                                    } catch (e: Exception) {
                                    }
                                }
                            }
            }

            try {
                (context as AppCompatActivity).findViewById<RecyclerView>(R.id.recycler_view).adapter?.notifyDataSetChanged()
            } catch (e: Exception) {
            }

            try {
                myFunctions.saveJson()
            } catch (e: Exception) {
            }
        }.start()


    }

}