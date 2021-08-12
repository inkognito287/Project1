package com.example.qrreader.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.R

class Error: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_error)
        findViewById<TextView>(R.id.text_dialog).text=intent.getStringExtra("error")
        findViewById<Button>(R.id.btn_ok).setOnClickListener(){
            finish()
        }
    }
}