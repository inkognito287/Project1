package com.example.qrreader

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class Functions(var context: Context) {

     fun readToFile(): String {

         return try {
             val reader =
                 BufferedReader(InputStreamReader(context.openFileInput("single.json")))
             val text = reader.readText()
             reader.close()
             text
         } catch (e: IOException) {
             Log.e("Exception", "File write failed: $e")
             "ERROR"
         }

    }

     fun writeToFile(jsonData: String?) {
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
    fun imageRequest(image: String, name: String, code: String, sharedPreferencesAddress:SharedPreferences): String? {


        val token = sharedPreferencesAddress.getString("token", "")
        val url = sharedPreferencesAddress.getString("address", "")
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", image)
            .addFormDataPart("name", name)
            .addFormDataPart("code", code)
            .build();

        var request = Request.Builder()
            .addHeader("token", token.toString())
            .url("$url/Account/image")
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
}