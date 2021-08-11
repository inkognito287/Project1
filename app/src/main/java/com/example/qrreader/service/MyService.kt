package com.example.qrreader.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.Constants.CHANNEL_ID
import com.example.qrreader.Constants.NOTIFICATION_ID
import com.example.qrreader.Pojo.Response
import com.example.qrreader.R
import com.example.qrreader.activities.MainActivity
import com.example.qrreader.fragment.array
import com.example.qrreader.fragment.myAdapter
import com.example.qrreader.fragment.myAdapterUpdate
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.util.*
import kotlin.concurrent.timerTask

class MyService : Service() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var timer: Timer
    var manager: NotificationManager? = null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        sharedPreferences =
            applicationContext.getSharedPreferences("address", Context.MODE_PRIVATE)!!
        timer = Timer()
        var timeTask = object : TimerTask() {
            override fun run() {
                try {
                    sendData()
                } catch (e: Exception) {
                }
            }
        }
        timer.schedule(timeTask, 0, 5000)

        createNotificationChannel()


        //start background
    }

    private fun sendData() {

        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wf = cm.activeNetwork
        if (wf!=null)

        Thread {
            try {
                val gson = Gson()

                val result = gson.fromJson(readToFile(applicationContext), Response::class.java)



                for (x in result.documents!!.size - 1 downTo 0) {
                    val last = result.documents[x]
                    if (last?.status == "no")
                        if (imageRequest(
                                last.photo.toString(),
                                last.day!! + " " + last.time!![0].toString() + last.time!![1].toString() + "-" + last.time!![3].toString() + last.time!![4].toString(),
                                last.documentFormatField!!
                            ) == "true"
                        ) {
                            result.documents[x]!!.status = "yes"
                            myAdapter.names1[x].status = "yes"
                        }
                    array?.clear()

                    for (x in 0..result.documents.size - 1)
                        array!!.add(result.documents[x]!!)
                    Log.d("MyLog", array.toString())
                    var s = 0
                    for (x in 0..result.documents.size - 1)
                        if (result.documents[x]!!.status == "no")
                            s++
                    if (s == 0) {
                        timer.cancel()
                        stopService(Intent(this, MyService::class.java))
                        manager!!.cancel(NOTIFICATION_ID)
                        if (myAdapter != null) {
                            myAdapterUpdate = myAdapter
                            myAdapterUpdate?.update()
                        }
                    }

                }

                val resultEnd = gson.toJson(result)
                writeToFile(resultEnd, applicationContext)

                (applicationContext as AppCompatActivity).runOnUiThread {
                    myAdapter?.update()
                }
            } catch (e: Exception) {

                Log.d("MyLog", "wifi exception=$e")

            }
        }.start()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentText("Отправка документов")
            .setSmallIcon(R.mipmap.ic_group6)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var serviceChannel = NotificationChannel(
                CHANNEL_ID, "My Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)

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

    private fun writeToFile(jsonData: String?, context: Context?) {
        try {
            val outputStreamWriter = OutputStreamWriter(
                context?.openFileOutput(
                    "single.json",
                    MODE_PRIVATE
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