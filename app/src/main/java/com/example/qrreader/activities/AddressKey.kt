package com.example.qrreader.activities


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.Functions
import com.example.qrreader.databinding.ActivityAddressKeyBinding
import com.example.qrreader.singletones.MySingleton
import com.example.qrreader.sslAllTrusted.Ssl
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.OutputStreamWriter
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*


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



                            val sslContext = SSLContext.getInstance("SSL")
                            sslContext.init(null, trustAllCerts, SecureRandom())
                            var  sslSocketFactory: SSLSocketFactory = sslContext.getSocketFactory();

                            val builder:OkHttpClient. Builder = OkHttpClient.Builder()

                            builder.sslSocketFactory(
                                sslSocketFactory,
                                trustAllCerts[0] as X509TrustManager
                            )
                            builder.hostnameVerifier(object : HostnameVerifier {
                                override fun verify(hostname: String?, session: SSLSession?): Boolean {
                                    return true
                                }
                            })

                            var client = Ssl().getUnsafeOkHttpClient()


                            val requestBody =  MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("password", key)
                                .build();


                            val request = Request.Builder()
                                .url("$address/Account/test")
                                .post(requestBody)
                                .build();

                            responseBody = try {
                                val response: Response = client!!.newCall(request).execute()
                                response.body?.string().toString()
                            } catch (e: Exception) {
                               ""
                            }
                            runOnUiThread {
                                Log.d("MyLog", "RESPONCEBODY=" + responseBody)
                            }
                            when (responseBody) {
                                "true" -> {
                                    MySingleton.urlForParsing = binding.editTextTextAddress.text.toString()
                                    sharedPreference.edit()
                                        .putString("key", binding.editTextTextKey.text.toString())
                                        .putString(
                                            "address",
                                            binding.editTextTextAddress.text.toString()

                                        ).apply()
                                    val intent = Intent(this@AddressKey, Authorization::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                "false" -> {
                                    runOnUiThread {
                                        binding.progressBarFirst.visibility = View.GONE
                                    }
                                    myFunctions.showError("Неправильный пароль")
                                    Log.d("MyLog", responseBody)

                                }
                                else -> {
                                    myFunctions.showError("Введённый сервер не отвечает")
                                }
                            }
                            runOnUiThread {
                                binding.progressBarFirst.visibility = View.GONE
                            }

                        } catch (e: Exception) {

                            runOnUiThread {

                                binding.progressBarFirst.visibility = View.GONE
                                //"Неверный формат адреса, ожидался URL типа 'http' или 'https' "
                                myFunctions.showError(e.toString())
                                Log.d("MyError","Неверный формат адреса, ожидался URL типа 'http' или 'https' ")
                            }
                        }
                    }.start()
                }
            }
        }
    }
    private val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
            }


            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String?) {
                try {
                    chain[0].checkValidity()
                } catch (e: java.lang.Exception) {
                    throw CertificateException("Certificate not valid or trusted.")
                }
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                return arrayOf()
            }



        }
    )

}