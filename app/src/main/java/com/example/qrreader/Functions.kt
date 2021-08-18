package com.example.qrreader

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.activities.Error
import com.example.qrreader.singletones.MySingleton
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*

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

    fun isNetworkAvailable(): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun getBitmapFromString(stringPicture: String): Bitmap? {
        val decodedString: ByteArray = android.util.Base64.decode(stringPicture, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
    fun saveJson(){
        val gson = Gson()
        val rootObject = JsonObject()
        val arrayObject = JsonArray()

        for (element in MySingleton.arrayList!!) {
            val childObject = JsonObject()
            // записываем текст в поле "message"
            childObject.addProperty("numberOfOrderField", element.numberOfOrderField)
            childObject.addProperty("documentFormatField", element.documentFormatField)
            childObject.addProperty("photo", element.stringImage)
            childObject.addProperty("day", element.day)
            childObject.addProperty("time", element.time)
            childObject.addProperty("status", element.status)
            childObject.addProperty("fullInformation",element.fullInformation)
            arrayObject.add(childObject)
            rootObject.add("documents", arrayObject)

            writeToFile(gson.toJson(rootObject))
        }
    }

     fun getStringFromBitmap(bitmapPicture: Bitmap): String? {
        val COMPRESSION_QUALITY = 100
        val encodedImage: String
        val byteArrayBitmapStream = ByteArrayOutputStream()
        bitmapPicture.compress(
            Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
            byteArrayBitmapStream
        )
        val b: ByteArray = byteArrayBitmapStream.toByteArray()
        encodedImage = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
        return encodedImage
    }


    fun showError(error: String) {
        var intent = Intent(context, Error::class.java)
        intent.putExtra("error", error)
        (context as AppCompatActivity).startActivity (intent)
    }

}