package com.example.qrreader.broadcastReceiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.qrreader.Functions
import com.example.qrreader.fragment.myAdapter
import com.example.qrreader.fragment.myAdapterUpdate
import com.example.qrreader.singletones.MySingleton


class MyBroadcastReceiver : BroadcastReceiver() {
    lateinit var sharedPreferencesAddress: SharedPreferences
    lateinit var sharedPreferencesUser: SharedPreferences
    lateinit var myFunctions: Functions

    @SuppressLint("UnsafeProtectedBroadcastReceiver", "ServiceCast")
    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferencesAddress = context?.getSharedPreferences("address", Context.MODE_PRIVATE)!!
        sharedPreferencesUser = context?.getSharedPreferences("user", Context.MODE_PRIVATE)!!
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


    private fun startSendImage(context: Context) {


        Thread {
            for (x in MySingleton.arrayList?.size!! - 1 downTo 0) {
                val last = MySingleton.arrayList!![x]
                if (last?.status == "no")
                    if (myFunctions.imageRequest(
                            last.stringImage.toString(),
                            last.day!! + " " + last.time!![0].toString() + last.time!![1].toString() + "-" + last.time!![3].toString() + last.time!![4].toString(),
                            last.documentFormatField!!,
                            sharedPreferencesAddress, sharedPreferencesUser
                        ) == "true"
                    ) {
                        MySingleton.arrayList!![x].status = "yes"

                    }


            }
            try {


                myAdapterUpdate = myAdapter

                myAdapterUpdate.update()
            } catch (e: Exception) {
            }

        }.start()


    }

}