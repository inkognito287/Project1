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
import okhttp3.RequestBody.Companion.asRequestBody
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
        type = type.replace(" ", "-")
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
        className: String,
        item: ItemForHistory?,
        bool: Boolean,
        numOfUrl: Int
    ): String? {
        var listOfFiles: ArrayList<File> = ArrayList()

        val token = sharedPreferencesUser.getString("token", "")
        var url = ""
        if (numOfUrl == 1)
            url = sharedPreferencesAddress.getString("address", "").toString()
        else if (numOfUrl == 2) url = MySingleton.secondUrl
        val client = Ssl().getUnsafeOkHttpClient()!!
        Log.d("MyLog", "CODE=" + code)
        var informationFromCodeRequest = Request.Builder()
            .addHeader("Authorization", "Bearer " + token.toString())
            .url( "$url/api/lead/?searchString=&status=&docNumber=${code.split("№")[1]}&dateFrom=&dateTo=&manager=&spam=&canceled=&pageSize=100&")
            .get()
            .build();


        try {
            val a = getInformationFromCode(informationFromCodeRequest, client)!!

            val gson = Gson()
            val resultOfParsing = gson.fromJson(a, DocumentInformation::class.java)

            for (x in 1..numberOfPages) {
                listOfFiles.add(
                    File(
                        Environment.getExternalStorageDirectory().absolutePath.toString() + "/",
                        item!!.documentFormatField[0]!!.split(",")[0].replace(" ", "-") +
                                "-<" +
                                code.split("№")[1] +
                                ">-<" +
                                x +
                                ">-<" +
                                numberOfPages +
                                ">" +
                                ".jpg"
                    )
                )
            }
            //Бланк-заказа-<номер заказа>-<номер страницы>-<всего страниц>.jpg
            val addIIDRequestBodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
            for (x in 0 until numberOfPages) {
                addIIDRequestBodyBuilder.addFormDataPart(
                    "files",
                    listOfFiles[x].name,
                    listOfFiles[x].asRequestBody("image/jpg".toMediaTypeOrNull())
                )
            }
            val addInformationInDatabaseRequestBody = addIIDRequestBodyBuilder.build();

            val closeLeadRequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("reason", "")
                .build()


            val addInformationInDatabaseRequest = Request.Builder()
                .addHeader("Authorization", "Bearer " + token.toString())
                .url(
                    "$url/Api/Attachment/UploadLeadDocument/?documentId=${
                        resultOfParsing.items?.get(
                            0
                        )?.id.toString()
                    }&close=${bool}"
                )
                .post(addInformationInDatabaseRequestBody)
                .build();

            Log.d("MyLog", "$className Информация по id ")

            try {
                var response = addInformationInDatabase(addInformationInDatabaseRequest, client)
                return response

            } catch (e: IOException) {
                Log.d("MyLog", "closeLeadEXCEpt" + e.toString())
                return "exception"

            }

        } catch (e: Exception) {
            Log.d("MyLog", "dontknow " + e.toString())
            return "exception"
        }
        return "exception"
    }

    private fun getInformationFromCode(request: Request, client: OkHttpClient): String? {
        try {
            val response: okhttp3.Response = client.newCall(request).execute()
            //var inf = response.body?.string()
            return response.body?.string()
        } catch (e: IOException) {
            Log.d("MyLog", "exception$e")

        }
        return "false"
    }

    private fun addInformationInDatabase(request: Request, client: OkHttpClient): String? {
        try {

            val response: okhttp3.Response = client.newCall(request).execute()

            return response.code.toString() + response.body?.string()
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
        val intent = Intent(context, Error::class.java)
        intent.putExtra("error", error)
        (context as AppCompatActivity).startActivity(intent)
    }


}