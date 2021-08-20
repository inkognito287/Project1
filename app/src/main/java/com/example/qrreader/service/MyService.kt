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
import com.example.qrreader.Functions
import com.example.qrreader.R
import com.example.qrreader.activities.MainActivity
import com.example.qrreader.singletones.MySingleton
import java.util.*

class MyService : Service() {
    lateinit var sharedPreferencesAddress: SharedPreferences
    lateinit var sharedPreferencesUser: SharedPreferences
    lateinit var timer: Timer
    lateinit var myFunction: Functions
    var manager: NotificationManager? = null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        myFunction = Functions(this.applicationContext)
        myFunction.saveJson()
        sharedPreferencesAddress =
            applicationContext.getSharedPreferences("address", Context.MODE_PRIVATE)!!

        sharedPreferencesUser =
            applicationContext.getSharedPreferences("user", Context.MODE_PRIVATE)!!

        timer = Timer()
        var timeTask = object : TimerTask() {
            override fun run() {
                try {
                    sendData()
                } catch (e: Exception) {
                }
            }
        }
        timer.schedule(timeTask, 10000, 15000)

        createNotificationChannel()

    }

    private fun sendData() {

        val cm =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wf = cm.activeNetwork
        if (wf != null)

            Thread {
                try {


                    for (x in MySingleton.arrayList!!.size - 1 downTo 0) {
                        val element = MySingleton.arrayList!![x]
                        if (element.status == "no")
                            if (myFunction.imageRequest(
                                    element.stringImage.toString(),
                                    element.day!! + " " + element.time!![0].toString() + element.time!![1].toString() + "-" + element.time!![3].toString() + element.time!![4].toString(),
                                    element.documentFormatField!!,
                                    sharedPreferencesAddress,
                                    sharedPreferencesUser
                                ) == "true"
                            ) { if (MySingleton.arrayList!![x].status == "no") {
                                MySingleton.arrayList!![x].status = "yes"
                                MySingleton.countUnsent.set(
                                    (MySingleton.countUnsent.get()!!.toInt() - 1).toString())



                            }

                            }
                    }
                    var unsentItems = 0
                    for (x in 0 until MySingleton.arrayList!!.size)
                        if (MySingleton.arrayList!![x]!!.status == "no")
                            unsentItems++

                    if (unsentItems == 0) {
                        timer.cancel()
                        stopService(Intent(this, MyService::class.java))
                        manager!!.cancel(NOTIFICATION_ID)
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
}