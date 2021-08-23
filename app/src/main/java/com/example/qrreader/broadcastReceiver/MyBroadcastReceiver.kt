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
//import com.example.qrreader.fragment.myAdapter
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
            for (y in MySingleton.arrayList?.size!! - 1 downTo 0) {
                val item = MySingleton.arrayList!![y]


                for (x in 0..item.stringImage!!.size - 1)
                    if (item.status[x] == "no")
                        if (myFunctions.imageRequest(
                                item.stringImage!![x],
                                item.day[x]!! + " " + item.time!![x][0].toString() + item.time!![x][1].toString() + "-" + item.time!![x][3].toString() + item.time!![x][4].toString(),
                                item.fullInformation[x],
                                sharedPreferencesAddress,
                                sharedPreferencesUser
                            ) == "true"
                        ) {
//                    MySingleton.countUnsent.set ((MySingleton.countUnsent.get()!!.toInt()-1).toString())
//                    if(MySingleton.countUnsent.get()=="0")
//                        runOnUiThread {
//                            //(binding.counterUnsent as View).visibility =View.GONE
//                            findViewById<View>(R.id.counter_unsent).visibility = View.GONE
//                        }
                           item.status[x] = "yes"


                        }
                     //   if (MySingleton.arrayList!![x].status == "no") {
                      //      MySingleton.arrayList!![x].status = "yes"
//                            MySingleton.countUnsent.set(
//                                (MySingleton.countUnsent.get()!!.toInt() - 1).toString()
//                            )
                        }




            try {


                myAdapterUpdate = myAdapter

                myAdapterUpdate.update()
            } catch (e: Exception) {
            }

        }.start()


    }

}