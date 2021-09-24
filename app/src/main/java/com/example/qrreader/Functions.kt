package com.example.qrreader


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.activities.Error
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.pojo.DocumentInformation
import com.example.qrreader.singletones.MySingleton
import com.example.qrreader.singletones.MySingleton.gson
import com.example.qrreader.sslAllTrusted.Ssl
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.*


class Functions(var context: Context) {

    fun readFromFile(): String {

        try {
            val reader =
                BufferedReader(InputStreamReader(context.openFileInput("single.json")))
            val text = reader.readLine()
            reader.close()
            if (text == null) {
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

    fun notAllSent(): Boolean {
        var unsentCount = 0
        for (x in 0 until MySingleton.arrayListOfBundlesOfDocuments!!.size) {
            var count = 0
            for (y in 0 until MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day.size)
                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.day[y] == null)
                    count++
            if (count == 0) {
                if (MySingleton.arrayListOfBundlesOfDocuments!![x]!!.status[0] == "no")
                    return true


            }
        }
        return false
    }


    fun saveBitmap(
        bmp: Bitmap,
        numberOfOrder: String,
        page: Int,
        allNumberOfPages: Int,
        documentFormatField: String
    ) {
        //numberOfOrder="Заказ №0871"
        //documentFormatField="Бланк заказа, стр. 1 из 1"
        var numb = numberOfOrder.split("№")[1]
        var type = documentFormatField.split(",")[0]
        type=type.replace(" ","-")
        val stream: FileOutputStream = FileOutputStream(
            File(
                Environment.getExternalStorageDirectory().absolutePath.toString() + "/",
                //Бланк-заказа-<номер заказа>-<номер страницы>-<всего страниц>.jpg
                "$type-<${numb}>-<${page}>-<$allNumberOfPages>" + ".jpg"
            )
        )
        bmp.compress(CompressFormat.JPEG, 100, stream)
        stream.close()

    }

    fun imageRequest(
        numberOfPages: Int,
        code: String,
        sharedPreferencesAddress: SharedPreferences,
        sharedPreferencesUser: SharedPreferences,
        className :String,
        item:ItemForHistory?
    ): String? {
        var listOfFiles:ArrayList<File> = ArrayList()

        val token = sharedPreferencesUser.getString("token", "")
        val url = sharedPreferencesAddress.getString("address", "")
        val client = Ssl().getUnsafeOkHttpClient()!!
        Log.d("MyLog","CODE="+code)
        var request2 = Request.Builder()
            .addHeader("Authorization", "Bearer " + token.toString())
            .url("$url/api/lead/?searchString=&status=&docNumber=${code.split("№")[1]}&dateFrom=&dateTo=&manager=&spam=&canceled=&pageSize=100&")
            .get()
            .build();


        try {
            var a = getInformationFromCode(request2, client)!!

            var gson = Gson()
            var responce = gson.fromJson(a, DocumentInformation::class.java)

            for (x in 1..numberOfPages) {
                listOfFiles.add(
                    File(
                        Environment.getExternalStorageDirectory().absolutePath.toString() + "/",
                         item!!.documentFormatField[0]!!.split(",")[0].replace(" ","-")+
                                "-<"+
                                code.split("№")[1] +
                                ">-<"+
                                 x+
                                ">-<"+
                                numberOfPages+
                                ">"+
                                ".jpg"
                    )
                )
            }
            //Бланк-заказа-<номер заказа>-<номер страницы>-<всего страниц>.jpg
            val kek = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
            for (x in 0..numberOfPages - 1) {
                kek.addFormDataPart(
                    "files",
                    listOfFiles[x].name,
                    RequestBody.create("image/jpg".toMediaTypeOrNull(), listOfFiles[x])
                )
            }
            val requestBody2 = kek.build();

            var requestBody4 =MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                //.addFormDataPart("id",responce.items?.get(0)?.id.toString())
                .addFormDataPart("reason","")
                .build()

            var request3 = Request.Builder()
                .addHeader("Authorization", "Bearer " + token.toString())
                .url("$url/Api/Attachment/{${responce.items?.get(0)?.id.toString()}}/?requestType=LEA")
                .post(requestBody2)
                .build();

            var closeLeadRequest = Request.Builder()
                .addHeader("Authorization", "Bearer " + token.toString())
                .url("$url/api/lead/close/${responce.items?.get(0)?.id.toString()}")
                .put(requestBody4)
                .build()


            Log.d("MyLog", "$className Информация по id ")

            try {
                var response = addInformationInDatabase(request3, client)
                if (response!="false")
                {
                    var c=closeLead(closeLeadRequest,client)
                    Log.d("MyLog","closeLead"+c.toString())
                    return response
                }

            } catch (e: IOException) {
                Log.d("MyLog","closeLeadEXCEpt"+e.toString())
                return "exception"

            }

        }catch (e:Exception){
            Log.d("MyLog","dontknow "+e.toString())
            return "exception"
        }
        return "exception"
    }

    private fun getInformationFromCode(request: Request, client: OkHttpClient): String? {
        try {
            val response: okhttp3.Response = client.newCall(request).execute()

            return response.body?.string()
        } catch (e: IOException) {
            Log.d("MyLog", "exception$e")

        }
        return "false"
    }
    private fun addInformationInDatabase(request: Request, client: OkHttpClient): String? {
        try {

            val response: okhttp3.Response = client.newCall(request).execute()

            return response.code.toString()+response.body?.string()
        } catch (e: IOException) {
            Log.d("MyLog", "exception$e")
        }
        return "false"
    }
    private fun closeLead(request: Request, client: OkHttpClient): String? {


        try {

            val response: okhttp3.Response = client.newCall(request).execute()

            return response.code.toString()+response.body?.string()
        } catch (e: IOException) {
            Log.d("MyLog", "exception$e")
        }
        return "false"

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

    fun saveJson() {

        var str = "{\"Response2\":" + gson.toJson(MySingleton.arrayListOfBundlesOfDocuments) + "}"
        writeToFile(str)
        Log.d("MyLog ", "savecompleted " + str)
    }

    fun showError(error: String) {
        var intent = Intent(context, Error::class.java)
        intent.putExtra("error", error)
        (context as AppCompatActivity).startActivity(intent)
    }


}