package com.example.qrreader


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun setting(v:View){
    val setting = SettingFragment()
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.fragmentContainerView,setting)
    fragmentTransaction.commit()


    }
    fun history(v: View){
     val history = HistoryFragment()
     val fragmentTransaction = supportFragmentManager.beginTransaction()
     fragmentTransaction.replace(R.id.fragmentContainerView,history)
     fragmentTransaction.commit()
    }
    fun finish(v: View){
      finish()
    }
}


