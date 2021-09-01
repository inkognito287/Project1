package com.example.qrreader.activities

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.R
import com.example.qrreader.service.MyService
import com.example.qrreader.singletones.MySingleton

class Error : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_error)
        findViewById<TextView>(R.id.text_dialog).text = intent.getStringExtra("error")
        MySingleton.countActivity = 3
        findViewById<Button>(R.id.btn_ok).setOnClickListener() {
            finish()
            MySingleton.countActivity = 1
        }
    }

    override fun onPause() {
        super.onPause()
        MySingleton.applicationIsActive = false
        Log.d("MyLog","Error is active="+MySingleton.applicationIsActive)
    }

    private fun isMyServiceRunning(myClass: Class<MyService>): Boolean {

        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager


        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {


            if (myClass.name.equals(service.service.className)) {

                return true

            }

        }
        return false
    }

    override fun onResume() {
        super.onResume()
        MySingleton.applicationIsActive = true
        if (isMyServiceRunning(MyService::class.java)) {
            stopService(Intent(this, MyService::class.java))
        }
    }
}