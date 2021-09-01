package com.example.qrreader.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.Functions

import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityAuthorizationBinding
import com.example.qrreader.pojo.User
import com.example.qrreader.singletones.MySingleton
import com.google.gson.Gson
//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import okhttp3.*
import java.lang.Exception
import java.net.URL


class Authorization : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesAddress: SharedPreferences
    lateinit var url: String
    lateinit var myFunctions: Functions
    var authorized = false
    private lateinit var binding: ActivityAuthorizationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        sharedPreferencesAddress = getSharedPreferences("address", Context.MODE_PRIVATE)

        myFunctions = Functions(this)
        url = sharedPreferencesAddress.getString("address", "").toString()
        if (sharedPreferences.contains("user")) {
            val intent = Intent(this@Authorization, MainActivity::class.java)
            startActivity(intent)
            authorized = true
            finish()
        }
        binding.enterButton.setOnClickListener {


            if (!myFunctions.isNetworkAvailable())
                myFunctions.showError("Проверьте подключение к интернету")
            else {

                if (!authorized)
                    request(
                        url,
                        binding.editTextPassword.text.toString(),
                        binding.editTextName.text.toString()
                    )
            }

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


    private fun request(url: String, password: String, name: String) {

        var token = sharedPreferencesAddress.getString("token", "")
        val fullUrl =
            URL("$url/Account/Token?username=$name&password=$password")
        binding.progressBarSecond.visibility = View.VISIBLE
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
                    .addHeader("token", token.toString())
                    .post(requestBody)
                    .build();

                responseBody = try {
                    val response: Response = client.newCall(request).execute()
                    response.body?.string().toString()
                } catch (e: Exception) {

                    "Сервер не отвечает"
                }
                if (responseBody != "{\"errorText\":\"Invalid username or password.\"}") {
                    var gson = Gson()
                    var result=gson.fromJson(responseBody,User::class.java)
                    var token = result.access_token
                    sharedPreferences.edit().putString("user", binding.editTextName.text.toString()).putString("token",token)
                        .apply()
                    val intent = Intent(this@Authorization, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    runOnUiThread {
                        myFunctions.showError("Неверные логин или пароль")
                    }

                }
            } catch (e: Exception) {

                Log.d("MyLog", e.toString())
            }
            runOnUiThread() {
                binding.progressBarSecond.visibility = View.GONE
            }
        }.start()

    }

    override fun onResume() {
        super.onResume()
        MySingleton.applicationIsActive = true

    }

    override fun onPause() {
        super.onPause()
        MySingleton.applicationIsActive = false
    }
}