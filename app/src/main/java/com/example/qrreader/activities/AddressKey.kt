package com.example.qrreader.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.CustomDialog
import com.example.qrreader.databinding.ActivityAddressKeyBinding
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.OutputStreamWriter


class AddressKey : AppCompatActivity() {


    lateinit var binding: ActivityAddressKeyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)


    


        try {
            val outputStreamWriter = OutputStreamWriter(
                openFileOutput(
                    "single.json",
                    MODE_PRIVATE
                )
            )
            outputStreamWriter.close()
        }catch (e:java.lang.Exception){}





        val sharedPreference = getSharedPreferences("address", Context.MODE_PRIVATE)
        if (sharedPreference.contains("key") && sharedPreference.contains("address")) {
            val intent = Intent(this@AddressKey, Authorization::class.java)
            startActivity(intent)
            finish()
        } else {
            binding.enterDataButton.setOnClickListener() {
                binding.progressBarFirst.visibility = View.VISIBLE
                Thread {

                    try {
                        val address = binding.editTextTextAddress.text.toString()

                        val key = binding.editTextTextKey.text.toString()
                        var responseBody = ""

                        val client = OkHttpClient()
                        val requestBody = MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("password", key)
                            .build();


                        val request = Request.Builder()
                            .url("$address/Account/test")
                            .post(requestBody)
                            .build();

                        responseBody = try {
                            val response: Response = client.newCall(request).execute()
                            response.body?.string().toString()
                        } catch (e: Exception) {
                            "false"
                        }
                        if (responseBody != "false") {

                            sharedPreference.edit()
                                .putString("key", binding.editTextTextKey.text.toString())
                                .putString(
                                    "address",
                                    binding.editTextTextAddress.text.toString()

                                ).putString("token",responseBody).apply()
                            val intent = Intent(this@AddressKey, Authorization::class.java)
                            startActivity(intent)
                        } else {

                            val alert = CustomDialog()
                            alert.showDialog(this, responseBody)
                            Log.d("MyLog", responseBody)
                        }


                    } catch (e: Exception) {

                        runOnUiThread {
                            binding.progressBarFirst.visibility = View.GONE
                            val alert = CustomDialog()
                            alert.showDialog(this, "Ошибка")
                        }

                    }
                }.start()


            }

        }
    }
}