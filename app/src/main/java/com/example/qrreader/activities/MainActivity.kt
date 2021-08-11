package com.example.qrreader.activities


import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.fragment.*
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityMainBinding
import com.example.qrreader.Functions
import com.example.qrreader.Pojo.DocumentsItem
import com.example.qrreader.service.MyService
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception


class MainActivity : AppCompatActivity() {


    private lateinit var myBroadcastReceiver: com.example.qrreader.broadcastReceiver.MyBroadcastReceiver
    lateinit var text: String
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences

    lateinit var myFragmentTransaction: MyFragmentTransaction
    lateinit var myFunctions: Functions
    //var kring : List<DocumentsItem?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("address", Context.MODE_PRIVATE)
        // kring= List<DocumentsItem>()


        array = ArrayList()
        myFunctions = Functions(applicationContext)
        myFragmentTransaction = MyFragmentTransaction(this)
        myBroadcastReceiver = com.example.qrreader.broadcastReceiver.MyBroadcastReceiver()
        val shardPreference = getSharedPreferences("user", Context.MODE_PRIVATE)

        // request()
        val filter = IntentFilter().apply {
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        this.registerReceiver(myBroadcastReceiver, filter)
        myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())
    }

    fun history(v: View) {
        myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())
    }

    fun camera(v: View) {
        val intent = Intent(this, BarcodeScanActivity::class.java)
        resultLauncher.launch(intent)

    }


    fun setting(v: View) {

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
            .setPositiveButton("Ок") { dialog, _ ->
                dialog.cancel()
                myAdapterUpdate = myAdapter
                if (myAdapter != null) {

                    array.clear()
                    myAdapterUpdate?.clear()
                }
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

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 28) {

                binding.button.isClickable=false
                Thread() {
                    val gson = Gson()
                    val res =
                        gson.fromJson(
                            myFunctions.readToFile(),
                            com.example.qrreader.Pojo.Response::class.java
                        )
                    array.clear()
                    for (x in 0 until res.documents?.size!!)
                        array.add(res.documents[x]!!)

                    runOnUiThread {


                        myAdapter?.notifyDataSetChanged()
                    }
//                       myAdapterUpdate= myAdapter
//                       myAdapterUpdate.updateRecyclerView(array)
//
//
//                   }

                    deserialize()


                }.start()

            }

        }


    private fun deserialize() {
//        runOnUiThread {
//            val gson = Gson()
//            val res =
//                gson.fromJson(
//                    myFunctions.readToFile(),
//                    com.example.qrreader.Pojo.Response::class.java
//                )
//            array?.clear()
//            for (x in 0 until res.documents?.size!!)
//                array.add(res.documents[x]!!)
//            myAdapter?.notifyDataSetChanged()
//
//        }

        Thread {
            val gson = Gson()

            val result = gson.fromJson(
                myFunctions.readToFile(),
                com.example.qrreader.Pojo.Response::class.java
            )

            val s = result.documents!!.size - 1


            val last = result.documents[s]
            try {


                if (last?.status == "no")
                    if (myFunctions.imageRequest(
                            last.photo.toString(),
                            last.day!! + " " + last.time!![0].toString() + last.time!![1].toString() + "-" + last.time!![3].toString() + last.time!![4].toString(),
                            last.numberOfOrderField!!,
                            sharedPreferences
                        ) == "true"
                    ) {
                        result.documents[s]!!.status = "yes"
                        val resultEnd = gson.toJson(result)
                        myFunctions.writeToFile(resultEnd)
                        runOnUiThread {
                            val gson = Gson()
                            val res =
                                gson.fromJson(
                                    myFunctions.readToFile(),
                                    com.example.qrreader.Pojo.Response::class.java
                                )
                            array?.clear()
                            for (x in 0 until res.documents?.size!!)
                                array.add(res.documents[x]!!)

                            myAdapterUpdate = myAdapter
                            myAdapterUpdate?.update()

                        }
                    }


            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            binding.button.isClickable=true
        }.start()
    }

    private fun isMyServiceRunning(myClass: Class<MyService>): Boolean {

        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager


        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {


            if (myClass.name.equals(service.service.className)) {

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







