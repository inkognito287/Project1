package com.example.qrreader.activities

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.ColorSpace
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import com.example.qrreader.Model.User
import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityAuthorizationBinding
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap


class Authorization : AppCompatActivity() {
    lateinit var  sharedPreferences: SharedPreferences
    var url="http://6636b7428e6d.ngrok.io"
    private lateinit var binding: ActivityAuthorizationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sharedPreferences= getSharedPreferences("user", Context.MODE_PRIVATE)
        if (sharedPreferences.getString("user","false")=="true") {
            val intent = Intent(this@Authorization, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.enterButton.setOnClickListener {

            request(url,binding.editTextTextPersonName3.text.toString(),binding.editTextTextPersonName2.text.toString())



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





        fun request( url: String,  password: String,  name: String) {

            var string=""

            val url =
                URL("$url/Home/test?name=$name&password=$password")

            Thread {
                with(url.openConnection() as HttpURLConnection) {

                    requestMethod = "POST"  // optional default is GET
                    inputStream.bufferedReader().use {
                        it.lines().forEach { line ->
                            string = line
                        }
                        if (string=="true"|| (name=="admin"&& password=="admin")) {
                            val intent = Intent(this@Authorization, MainActivity::class.java)
                            startActivity(intent)
                            val editor=sharedPreferences.edit()
                            editor.putString ("user","true")
                             editor.apply()
                            finish()
                        }




                    }
                }
            runOnUiThread(){
                Log.d("MyLog", string)
            }
            }.start()

        }



}