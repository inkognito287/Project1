package com.example.qrreader.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.R
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

        if (binding.editTextTextPersonName3.transformationMethod == null) {
            binding.imageViewEye.setImageResource(R.drawable.authorization_eye)
            binding.editTextTextPersonName3.transformationMethod = PasswordTransformationMethod()

        } else {
            binding.imageViewEye.setImageResource(R.drawable.authorization_close_eye)
            binding.editTextTextPersonName3.transformationMethod = null
        }
    }
}