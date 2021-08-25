package com.example.qrreader

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.activities.Error
import com.example.qrreader.singletones.MySingleton
import com.example.qrreader.singletones.MySingleton.gson
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import android.graphics.Bitmap.CompressFormat




class Functions(var context: Context) {

    fun readFromFile(): String {

         try {
            val reader =
                BufferedReader(InputStreamReader(context.openFileInput("single.json")))
            val text = reader.readLine()
            reader.close()
             if(text==null){
                 return "ERROR"
             }
             return text
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
             return "ERROR"
        }

    }

    private fun writeToFile(jsonData: String?) {
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

    private fun writeToFileEnd(jsonData: String?) {
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

    fun saveBitmap(bmp: ArrayList<Bitmap?>, numberOfOrder: String?) {

        var numb = numberOfOrder!!.split("№")[1]
        var page=1
        for (element in bmp) {
            Log.d("MyLog",Environment.getExternalStorageDirectory().absolutePath.toString()+"/"+"${numb}page${page}"+".png")
            val stream: FileOutputStream = FileOutputStream(File(Environment.getExternalStorageDirectory().absolutePath.toString()+"/","${numb}page${page}"+".png"))

            element!!.compress(CompressFormat.PNG, 70, stream) // пишем битмап на PNG с качеством 70%

            page++
            stream.close()
        }

    }

    fun imageRequest(
        image: String,
        name: String,
        code: String,
        sharedPreferencesAddress: SharedPreferences,
        sharedPreferencesUser: SharedPreferences
    ): String? {


        val token = sharedPreferencesUser.getString("token", "")
        val url = sharedPreferencesAddress.getString("address", "")
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", image)
            .addFormDataPart("name", name)
            .addFormDataPart("code", code)
            .build();

        var request = Request.Builder()
            .addHeader("Authorization", "Bearer " + token.toString())
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
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
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


    fun getBitmapFromString(stringPictures: ArrayList<String>): ArrayList<Bitmap> {
        var arrayListBitmap = ArrayList<Bitmap>()
        var decodedString: ByteArray? = null
        for (element in stringPictures) {
            decodedString = android.util.Base64.decode(element, Base64.DEFAULT)
            arrayListBitmap.add(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size))
        }

        return arrayListBitmap
    }


    fun saveJson() {

//
//
//            val childObject = JsonObject()
////             записываем текст в поле "message"
////            childObject.addProperty("numberOfOrderField", element.numberOfOrderField)
////            childObject.addProperty("documentFormatField", element.documentFormatField)
////            childObject.addProperty("photo", element.stringImage)
////            childObject.addProperty("day", element.day)
////            childObject.addProperty("time", element.time)
////            childObject.addProperty("status", element.status)
////            childObject.addProperty("fullInformation",element.fullInformation)

//            rootObject.add("documents", arrayObject)

        //var str = "{\"Response2\":"+gson.toJson(MySingleton.arrayList)+"}"
        var str ="{\"Response2\":"+gson.toJson(MySingleton.arrayList)+"}"
        writeToFile(str)
        Log.d("MyLog ","savecompleted "+str )
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
        (context as AppCompatActivity).startActivity(intent)
    }

}