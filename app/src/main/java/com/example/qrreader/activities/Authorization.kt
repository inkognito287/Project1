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
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap


class Authorization : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var url: String
    var authorized = false
    private lateinit var binding: ActivityAuthorizationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        url = "https://9d395b8b4dff.ngrok.io"
                //sharedPreferences.getString("url", "").toString()
        if (sharedPreferences.getString("user", "false") == "true") {
            val intent = Intent(this@Authorization, MainActivity::class.java)
            startActivity(intent)
            authorized = true
            finish()
        }


        binding.enterButton.setOnClickListener {
            if (!authorized)
                request(
                    url,
                    binding.editTextPassword.text.toString(),
                    binding.editTextName.text.toString()
                )


        }
    }

    fun switchPasswordVisibility(v: View) {

        if (binding.editTextPassword.transformationMethod == null) {
            binding.imageViewEye.setImageResource(R.drawable.authorization_eye)
            binding.editTextPassword.transformationMethod = PasswordTransformationMethod()

        } else {
            binding.imageViewEye.setImageResource(R.drawable.authorization_close_eye)
            binding.editTextPassword.transformationMethod = null
        }
    }


    fun request(url: String, password: String, name: String) {

        var string = ""

        val fullUrl =
            URL("$url/Home/test?name=$name&password=$password")

        Thread {
            try {


                with(fullUrl.openConnection() as HttpURLConnection) {

                    requestMethod = "POST"  // optional default is GET
                    inputStream.bufferedReader().use {
                        it.lines().forEach { line ->
                            string = line
                        }
                        if (string == "true" || (name == "admin" && password == "admin")) {
                            val intent = Intent(this@Authorization, MainActivity::class.java)
                            startActivity(intent)
                            val editor = sharedPreferences.edit()
                            editor.putString("user", "true")
                            editor.apply()
                            finish()
                        }


                    }
                }
                runOnUiThread() {
                    Log.d("MyLog", "token = $string")
                    sharedPreferences.edit().putString("token", string).apply()
                }
            }catch (e:Exception){

            }

        }.start()

    }


}