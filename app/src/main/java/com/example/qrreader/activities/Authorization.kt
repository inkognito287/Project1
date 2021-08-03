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
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.HashMap


class Authorization : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPreferencesAdress: SharedPreferences
    lateinit var url: String
    var authorized = false
    private lateinit var binding: ActivityAuthorizationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        sharedPreferencesAdress = getSharedPreferences("address",Context.MODE_PRIVATE)

        url = sharedPreferencesAdress.getString("address", "").toString()
        //sharedPreferences.getString("url", "").toString()
        if (sharedPreferences.contains("token")) {
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
            URL("$url/Account/test?name=$name&password=$password")

        Thread {
            try {


                var responseBody = ""
                val name = binding.editTextName.text.toString()
                val password = binding.editTextPassword.text.toString()
                val client = OkHttpClient()
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("name", name)
                    .addFormDataPart("password", password)
                    .build();


                val request = Request.Builder()
                    .url(fullUrl)
                    .post(requestBody)
                    .build();

                try {
                    val response: Response = client.newCall(request).execute()
                    responseBody = response.body?.string().toString()
                } catch (e: Exception) {

                    responseBody = "false"
                }
                if (responseBody != "error") {
                    sharedPreferences.edit().putString("token", responseBody).apply()
                    val intent = Intent(this@Authorization, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {

            }

        }.start()

    }


}