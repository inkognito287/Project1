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
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.qrreader.CustomRecyclerAdapter
import com.example.qrreader.Interfaces.UpdateAdapter
import com.example.qrreader.Pojo.Response
import com.example.qrreader.fragment.array
import com.example.qrreader.fragment.myAdapter
import com.example.qrreader.fragment.myAdapterUpdate
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception


class MyBroadcastReceiver : BroadcastReceiver() {
    lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("UnsafeProtectedBroadcastReceiver", "ServiceCast")
    override fun onReceive(context: Context?, intent: Intent?) {
         sharedPreferences = context?.getSharedPreferences("user", Context.MODE_PRIVATE)!!
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wf = cm.activeNetwork
        val activeNetwork = cm.getNetworkCapabilities(cm.activeNetwork)
            ?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
       // val activeWifi = wf.getNetworkCapabilities(wf.activeNetwork)?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        if (wf!=null){


            Thread {try {
                val gson = Gson()

                val result = gson.fromJson(readToFile(context), Response::class.java)

                var s = result.documents!!.size

                for (x in result.documents.size - 1 downTo 0) {
                    val last = result.documents[x]
                    if (last?.status == "no")
                        if (imageRequest(
                                last.photo.toString(),
                                last.day!! + " " + last.time!![0].toString() + last.time!![1].toString() + "-" + last.time!![3].toString() + last.time!![4].toString(),
                                last.code!!
                            ) == "true"
                        ) {
                            result.documents[x]!!.status = "yes"

                        }
                    array?.clear()

                    for (x in 0..result.documents.size-1)
                      array!!.add(result.documents[x]!!)

                    Log.d("MyLog",array.toString())

                }

                val resultEnd = gson.toJson(result)
                writeToFile(resultEnd, context)

                (context as AppCompatActivity).runOnUiThread {
                    myAdapter.update()
                }
            }catch (e:Exception){

                Log.d("MyLog","wifi exception=$e")

            }
            }.start()

        }
        else
        if (activeNetwork == true) {




                Thread {try {
                    val gson = Gson()

                    val result = gson.fromJson(readToFile(context), Response::class.java)

                    var s = result.documents!!.size

                    for (x in result.documents.size - 1 downTo 0) {
                        val last = result.documents[x]
                        if (last?.status == "no")
                            if (imageRequest(
                                    last.photo.toString(),
                                    last.day!! + " " + last.time!![0].toString() + last.time!![1].toString() + "-" + last.time!![3].toString() + last.time!![4].toString(),
                                    last.code!!
                                ) == "true"
                            ) {
                                result.documents[x]!!.status = "yes"

                                //myAdapter?.notifyDataSetChanged()

                            }
                        array?.clear()
                        for (x in 0 until result.documents?.size!!)
                            array?.add(result.documents[x]!!)
                        myAdapter.update()

                    }

                    val resultEnd = gson.toJson(result)
                    writeToFile(resultEnd, context)
                }catch (e:Exception){}
                }.start()




        }
    }


    fun imageRequest(image: String, name: String, code: String): String? {


        val token = sharedPreferences.getString("token", "")
        val url = sharedPreferences.getString("address", "")
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", image)
            .addFormDataPart("name", name)
            .addFormDataPart("code", code)
            .build();

        var request = Request.Builder()
            .addHeader("token", token.toString())
            .url("$url/Home/image")
            .post(requestBody)
            .build();


        try {
            val response: okhttp3.Response = client.newCall(request).execute()

            return response.body?.string()
        } catch (e: IOException) {
            Log.d("MyLog", "exception$e")

        }


        return null

    }

    private fun readToFile(context: Context?): String {

        return try {
            val reader =
                BufferedReader(InputStreamReader(context?.openFileInput("single.json")))
            val text = reader.readText()
            reader.close()
            text
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
            "ERROR"
        }

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
            Log.e("Exception", "File write failed: $e")
        }
    }
}