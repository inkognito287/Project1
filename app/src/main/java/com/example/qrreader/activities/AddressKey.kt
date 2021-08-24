package com.example.qrreader.activities

import android.content.Context
import android.content.Intent
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.Functions
import com.example.qrreader.databinding.ActivityAddressKeyBinding
//import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.OutputStreamWriter


class AddressKey : AppCompatActivity() {

    lateinit var myFunctions: Functions
    lateinit var binding: ActivityAddressKeyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreference = getSharedPreferences("address", Context.MODE_PRIVATE)
        if (sharedPreference.contains("key") && sharedPreference.contains("address")) {
            val intent = Intent(this@AddressKey, Authorization::class.java)
            startActivity(intent)
            finish()
        } else {
            myFunctions = Functions(this)

            try {

                val outputStreamWriter = OutputStreamWriter(
                    openFileOutput(
                        "single.json",
                        MODE_PRIVATE
                    )
                )
                outputStreamWriter.close()
            } catch (e: java.lang.Exception) {
            }
            binding.enterDataButton.setOnClickListener() {


                if (!myFunctions.isNetworkAvailable())
                    myFunctions.showError("Проверьте подключение к интернету")
                else if (binding.editTextTextAddress.text.toString()==""||binding.editTextTextKey.text.toString()=="")
                    myFunctions.showError("Проверьте введённые данные")
                else {

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
                                ""
                            }
                            runOnUiThread {
                                Log.d("MyLog", "RESPONCEBODY=" + responseBody)
                            }
                            if (responseBody == "true") {

                                sharedPreference.edit()
                                    .putString("key", binding.editTextTextKey.text.toString())
                                    .putString(
                                        "address",
                                        binding.editTextTextAddress.text.toString()

                                    ).apply()
                                val intent = Intent(this@AddressKey, Authorization::class.java)
                                startActivity(intent)
                                finish()
                            } else if (responseBody == "false") {
                                runOnUiThread {
                                    binding.progressBarFirst.visibility = View.GONE
                                }
                                myFunctions.showError("Неправильный пароль")
                                Log.d("MyLog", responseBody)

                            } else {
                                myFunctions.showError("Введённый сервер не отвечает")
                            }
                            runOnUiThread {
                                binding.progressBarFirst.visibility = View.GONE
                            }

                        } catch (e: Exception) {

                            runOnUiThread {

                                binding.progressBarFirst.visibility = View.GONE
                                myFunctions.showError("Неверный формат адреса, ожидался URL типа 'http' или 'https' ")
                                Log.d("MyError","Неверный формат адреса, ожидался URL типа 'http' или 'https' ")
                            }
                        }
                    }.start()
                }
            }
        }
    }
}