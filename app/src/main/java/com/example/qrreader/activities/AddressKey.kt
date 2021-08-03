package com.example.qrreader.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityAddressKeyBinding
import com.example.qrreader.fragment.HistoryFragment
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.HttpURLConnection
import java.net.URL

class AddressKey : AppCompatActivity() {


    lateinit var binding:ActivityAddressKeyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreference=getSharedPreferences("address", Context.MODE_PRIVATE)
        if (sharedPreference.contains("key") && sharedPreference.contains("address")) {
            val intent = Intent (this@AddressKey,Authorization::class.java)
            startActivity(intent)
            finish()
        }
         else {
            binding.enterDataButton.setOnClickListener() {
                Thread {
                    var responseBody = ""
                    val address = binding.editTextTextAddress.text.toString()
                    val key = binding.editTextTextKey.text.toString()
                    val client = OkHttpClient()
                    val requestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("key", key)
                        .build();


                    val request = Request.Builder()
                        .url("$address/Account/testService?key=$key")
                        .post(requestBody)
                        .build();

                    try {
                        val response: Response = client.newCall(request).execute()
                        responseBody = response.body?.string().toString()
                    } catch (e: Exception) {
                        responseBody="false"
                    }
                    if (responseBody!="false")
                    {

                        sharedPreference.edit().putString("key", binding.editTextTextKey.text.toString())
                            .putString(
                                "address",
                                binding.editTextTextAddress.text.toString()

                            ).apply()
                        val intent = Intent (this@AddressKey,Authorization::class.java)
                        startActivity(intent)
                    }
                    else {
                        Log.d("MyLog", "error")
                    }

                }.start()



            }

    }
}
}