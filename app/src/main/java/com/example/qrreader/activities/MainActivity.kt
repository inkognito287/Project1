package com.example.qrreader.activities


import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.qrreader.fragment.*
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityMainBinding
import com.example.qrreader.service.MyService
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception


class MainActivity : AppCompatActivity() {


    private lateinit var myBroadcastReceiver: com.example.qrreader.broadcastReceiver.MyBroadcastReceiver
    lateinit var text: String
    lateinit var binding: ActivityMainBinding


    lateinit var myFragmentTransaction: MyFragmentTransaction


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        array = ArrayList()
        myFragmentTransaction = MyFragmentTransaction(this)
        myBroadcastReceiver = com.example.qrreader.broadcastReceiver.MyBroadcastReceiver()
        val shardPreference = getSharedPreferences("user", Context.MODE_PRIVATE)

        // request()
        val filter = IntentFilter().apply {
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        this.registerReceiver(myBroadcastReceiver, filter)
        //fragmentTransactionReplace(historyFragment)
        myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())
    }

    fun history(v: View) {
        //fragmentTransactionReplace(historyFragment)
        myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())
    }

    fun camera(v: View) {
        val intent = Intent(this, BarcodeScanActivity::class.java)
        //startActivityForResult(intent, 28)
        resultLauncher.launch(intent)

    }


    fun setting(v: View) {

        //  fragmentTransactionReplace(SettingFragment())
        myFragmentTransaction.fragmentTransactionReplace(SettingFragment())
    }

    fun finish(v: View) {
        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear().apply()
        val intent = Intent(this, Authorization::class.java)
        startActivity(intent)
        finish()
    }

    fun back(v: View) {
        onBackPressed()
    }

    fun secure(v: View) {
        myFragmentTransaction.fragmentTransactionReplace(SecureFragment())
    }

    fun data(v: View) {
        val data = DataFragment()
        myFragmentTransaction.fragmentTransactionReplace(data)
    }

    fun clearHistory(v: View) {

        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("Очистка истории")
            .setMessage("Вы уверены, что хотите очистить историю? Неотправленные данные будут удалены")
            .setIcon(R.drawable.clear_history)
            .setPositiveButton("Ок") { dialog, id ->
                dialog.cancel()
                myAdapterUpdate = myAdapter
                if (myAdapter != null)
                    myAdapterUpdate?.clear()


            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create()
        builder.show()

    }

    fun historyBack(v: View) {
        finish()
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 1)
            supportFragmentManager.popBackStack()
        else finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data?.getIntExtra("fragment", 1) == 1)
                    myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())
                else if (data?.getIntExtra("fragment", 1) == 2)
                    myFragmentTransaction.fragmentTransactionReplace(SettingFragment())
            } else if (result.resultCode == 28) {
                val data: Intent? = result.data
                var zxf = readToFile()
                val gson = Gson()
                val res =
                    gson.fromJson(readToFile(), com.example.qrreader.Pojo.Response::class.java)
                for (x in 0 until res.documents?.size!!)
                    array?.add(res.documents[x]!!)
                myAdapter?.notifyDataSetChanged()
                myFragmentTransaction.fragmentTransactionReplace(SettingFragment())

                myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())
                deserialize()

            }

        }


    @RequiresApi(Build.VERSION_CODES.O)
    fun deserialize() {
        Thread {
            val gson = Gson()

            val result = gson.fromJson(readToFile(), com.example.qrreader.Pojo.Response::class.java)

            val s = result.documents!!.size - 1


            val last = result.documents[s]
            try {


                if (last?.status == "no")
                    if (imageRequest(
                            last.photo.toString(),
                            last.day!! + " " + last.time!![0].toString() + last.time!![1].toString() + "-" + last.time!![3].toString() + last.time!![4].toString(),
                            last.numberOfOrderField!!
                        ) == "true"
                    ) {
                        result.documents[s]!!.status = "yes"
                        val resultEnd = gson.toJson(result)
                        writeToFile(resultEnd, this)
                        runOnUiThread {
                            val gson = Gson()
                            val res =
                                gson.fromJson(
                                    readToFile(),
                                    com.example.qrreader.Pojo.Response::class.java
                                )
                            array?.clear()
                            for (x in 0 until res.documents?.size!!)
                                array?.add(res.documents[x]!!)
                            myAdapter?.notifyDataSetChanged()

                        }
                    }


            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }

        }.start()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun readToFile(): String {

        try {
            val reader =
                BufferedReader(InputStreamReader(openFileInput("single.json")))
            val text = reader.readText()
            reader.close()
            return text
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
            return "ERROR"
        }

    }

    private fun writeToFile(jsonData: String?, context: Context?) {
        try {
            val outputStreamWriter = OutputStreamWriter(
                context?.openFileOutput(
                    "single.json",
                    MODE_PRIVATE
                )
            )
            outputStreamWriter.write(jsonData)

            outputStreamWriter.close()
            println("good")
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }


    private fun imageRequest(image: String, name: String, code: String): String? {
        val sharedPreferences = getSharedPreferences("address", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        val sharedPreferencesAddress = getSharedPreferences("address", Context.MODE_PRIVATE)
        val url = sharedPreferencesAddress.getString("address", "")


        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", image.toString())
            .addFormDataPart("name", name.toString())
            .addFormDataPart("code", code.toString())
            .build();

        val request = Request.Builder()
            .addHeader("token", token.toString())
            .url("$url/Account/image")
            .post(requestBody)
            .build();


        try {
            val response: Response = client.newCall(request).execute()
            Log.d("MyLog", "rabotaet")
            return response.body?.string()


            // Do something with the response.
        } catch (e: IOException) {
            Log.d("MyLog", "exception$e")
            return null
        }


    }

    private fun isMyServiceRunning(mclass: Class<MyService>): Boolean {

        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager


        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {


            if (mclass.name.equals(service.service.className)) {

                return true

            }

        }
        return false
    }

    private fun checkStatus(): Boolean {


        if (myAdapter != null) {
            var s = 0
            for (x in 0 until myAdapter!!.names1.size)
                if (myAdapter!!.names1[x].status == "no")
                    s++
            if (s > 0)

                return true
        }
        return false

    }

    override fun onStop() {
        super.onStop()
        Log.d("life", "Stop")
        Thread() {
            if (checkStatus())
                if (!isMyServiceRunning(MyService::class.java)) {
                    startService(Intent(this, MyService::class.java))
                }
        }.start()
    }

    override fun onResume() {
        super.onResume()

        Log.d("life", "resume")
        if (isMyServiceRunning(MyService::class.java)) {
            stopService(Intent(this, MyService::class.java))
        }
    }


}







