package com.example.qrreader.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.Pojo.Response
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        var cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetwork = cm.getNetworkCapabilities(cm.activeNetwork)
            ?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        if (activeNetwork == true) {


            Thread {
                val gson = Gson()

                var result = gson.fromJson(readToFile(context), Response::class.java)

                var s = result.documents!!.size

                for (x in result.documents!!.size - 1 downTo 0) {
                    var last = result.documents!![x]
                    if (imageRequest(
                            last?.photo.toString(),
                            last?.day!! + " " + last.time!![0].toString() + last.time!![1].toString() + "-" + last.time!![3].toString() + last.time!![4].toString(),
                            last.code!!
                        ) == "true"
                    ) {
                       result.documents!![x]!!.status="yes"

                    }
                }

                var resultEnd=gson.toJson(result.documents)
                writeToFile(resultEnd,context)

            }.start()


        }
    }


    fun imageRequest(image: String, name: String, code: String): String? {

        var token = "rerere"
        var client = OkHttpClient()
        var requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", image.toString())
            .addFormDataPart("name", name.toString())
            .addFormDataPart("code", code.toString())
            .build();

        var request = Request.Builder()
            .addHeader("token", token)
            .url("http://86.57.171.246:7777/Home/image")
            .post(requestBody)
            .build();


        try {
            val response: okhttp3.Response = client.newCall(request).execute()
            Log.d("MyLog", "image send = " + response.body!!.string())
            return response.body?.string()

            // Do something with the response.
        } catch (e: IOException) {
            Log.d("MyLog", "exception" + e.toString())

        }


        return null

    }

    private fun readToFile(context: Context?): String {

        try {
            val reader =
                BufferedReader(InputStreamReader(context?.openFileInput("single.json")))
            val text = reader.readText()
            reader.close()
            return text
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
            return "ERROR"
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
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }
}