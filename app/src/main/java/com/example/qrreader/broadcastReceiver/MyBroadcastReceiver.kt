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
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.Functions
import com.example.qrreader.R
import com.example.qrreader.singletones.MySingleton

//import com.example.qrreader.fragment.myAdapter
import java.util.*


class MyBroadcastReceiver : BroadcastReceiver() {
    lateinit var sharedPreferencesAddress: SharedPreferences
    lateinit var sharedPreferencesUser: SharedPreferences
    lateinit var myFunctions: Functions
    lateinit var mySingleton:MySingleton

    @SuppressLint("UnsafeProtectedBroadcastReceiver", "ServiceCast")
    override fun onReceive(context: Context?, intent: Intent?) {
        sharedPreferencesAddress = context?.getSharedPreferences("address", Context.MODE_PRIVATE)!!
        sharedPreferencesUser = context.getSharedPreferences("user", Context.MODE_PRIVATE)!!
        myFunctions = Functions(context)
        mySingleton=MySingleton()
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


    @SuppressLint("NotifyDataSetChanged")
    private fun startSendImage(context: Context) {


        Thread {


            for (y in mySingleton.arrayListOfBundlesOfDocuments?.size!! - 1 downTo 0) {
                val document = mySingleton.arrayListOfBundlesOfDocuments!![y]
                var inf = document?.documentFormatField!![0]
                var bool = false

                bool = inf!!.split(",")[0] == "Бланк заказа" || inf.split(",")[0] == "УПД"
                var count = 0
                for (numberOfStatusField in 0 until document!!.status.size)
                    if (document.status[numberOfStatusField] == null)
                        count++


                if (count == 0){
                    var x=0
                        if (document.status[x] == "no")
                            if (myFunctions.imageRequest(document.documentFormatField.size,
                                    document.fullInformation!!,
                                    sharedPreferencesAddress,
                                    sharedPreferencesUser,
                                    "BroadcastReciever",
                                    document,bool
                                ) != "exception"
                            ) {
                                document.status[0] = "yes"
                                mySingleton.countUnsent.set(
                                    (mySingleton.countUnsent.get()!!.toInt() - 1).toString()
                                )
                            }}
            }

            try {
                (context as AppCompatActivity).runOnUiThread(){
                    try {


                        var adapter =
                            (context as AppCompatActivity).findViewById<RecyclerView>(R.id.recycler_view).adapter
                        if (adapter != null)
                            adapter.notifyDataSetChanged()
                    }catch (e:Exception){}
                }
            } catch (e: Exception) {
                Log.d("MyLog",e.toString())
            }

            try {
                myFunctions.saveJson()
            } catch (e: Exception) {
            }
        }.start()


    }

}

private fun Timer.schedule(function: () -> Unit, i: Int) {

}
