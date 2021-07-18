package com.example.qrreader

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.databinding.ActivityAuthorizationBinding

class Authorization : AppCompatActivity() {


    private lateinit var binding: ActivityAuthorizationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.enterButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun switchPasswordVisibility(v: View) {
        binding.editTextTextPersonName3.transformationMethod =
            if (binding.editTextTextPersonName3.transformationMethod == null) PasswordTransformationMethod() else null
    }
}