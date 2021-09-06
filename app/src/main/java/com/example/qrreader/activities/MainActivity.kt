package com.example.qrreader.activities



import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.example.qrreader.Functions
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityMainBinding
import com.example.qrreader.fragment.*
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.service.MyService
import com.example.qrreader.singletones.MySingleton
import com.google.gson.Gson
import java.io.File
import java.io.OutputStreamWriter


class MainActivity : AppCompatActivity() {


    private lateinit var myBroadcastReceiver: com.example.qrreader.broadcastReceiver.MyBroadcastReceiver
    lateinit var text: String
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferencesAddress: SharedPreferences
    lateinit var sharedPreferencesUser: SharedPreferences

    lateinit var myFragmentTransaction: MyFragmentTransaction
    lateinit var myFunctions: Functions


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesAddress = getSharedPreferences("address", Context.MODE_PRIVATE)
        sharedPreferencesUser = getSharedPreferences("user", Context.MODE_PRIVATE)

        MySingleton.arrayListOfBundlesOfDocuments = ArrayList()
        MySingleton.countUnsent = ObservableField()
        findViewById<View>(R.id.counter_unsent).visibility = View.GONE

        MySingleton.image = java.util.ArrayList()
        MySingleton.title = java.util.ArrayList()
        MySingleton.text = String()
        MySingleton.image = java.util.ArrayList()
        MySingleton.day = java.util.ArrayList()
        MySingleton.time = java.util.ArrayList()
        MySingleton.status = java.util.ArrayList()


        MySingleton.countUnsent.addOnPropertyChangedCallback(observableChangeCallback)


        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )

        myFunctions = Functions(applicationContext)
        binding.progressBarMainActivity.visibility = View.VISIBLE
        Thread {
            MySingleton.arrayListOfBundlesOfDocuments = ArrayList()
            val gson = Gson()

            var textFromFile = myFunctions.readFromFile()

            if (textFromFile != "" && textFromFile != "ERROR") {

                try {
                    val result =
                        gson.fromJson(textFromFile, com.example.qrreader.pojo.Response2::class.java)

                    for (element in result.response2!!)
                        MySingleton.arrayListOfBundlesOfDocuments!!.add(
                            ItemForHistory(
                                element!!.documentFormatField as ArrayList<String?>,
                                element.numberOfOrderField,
                                element.day as ArrayList<String?>,
                                element.time as ArrayList<String?>,
                                element.status as ArrayList<String?>,
                                element.fullInformation
                            )
                        )


                } catch (e: Exception) {
                    Log.d("MyLog", e.toString())
                }
            }

            runOnUiThread {
                var countOfUnsentPacksOfDocuments = 0

                try {


                    for (element in MySingleton.arrayListOfBundlesOfDocuments!!) {
                        var notNull = 0
                        for (fieldOfElement in element!!.day) {
                            if (fieldOfElement != null)
                                notNull++
                        }
                        if (notNull == element.day.size)

                            if (element.status[0] == "no")
                                countOfUnsentPacksOfDocuments++
                    }

                } catch (e: Exception) {
                }
                MySingleton.countUnsent.set(countOfUnsentPacksOfDocuments.toString())
                binding.count = MySingleton.countUnsent
            }
            myFragmentTransaction = MyFragmentTransaction(this)
            myBroadcastReceiver = com.example.qrreader.broadcastReceiver.MyBroadcastReceiver()

            runOnUiThread {
                myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())
                binding.progressBarMainActivity.visibility = View.GONE
            }

            val filter = IntentFilter().apply {
                addAction("android.net.conn.CONNECTIVITY_CHANGE")
                addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            }
            this.registerReceiver(myBroadcastReceiver, filter)
        }.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1)
            if (!isExternalPermissionGranted()) {
                finish()
            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun history(v: View) {
        myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())
    }

    fun camera(v: View) {

        val intent = Intent(this, BarcodeScanActivity::class.java)
        resultLauncher.launch(intent)

    }

    private fun isExternalPermissionGranted(): Boolean {
        return (ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED)
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
                MySingleton.countUnsent.set("0")
                try {
                    clear()
                    findViewById<RecyclerView>(R.id.recycler_view).adapter!!.notifyDataSetChanged()
                }catch (e:java.lang.Exception){

                }
                try {
                    val dir =
                        File(Environment.getExternalStorageDirectory().absolutePath)
                    if (dir.isDirectory) {
                        val children: Array<String> = dir.list()
                        for (i in children.indices) {
                            File(dir, children[i]).delete()
                        }
                    }
                } catch (e: Exception) {
                    Log.d("MyLog", e.toString())
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
    private fun clear(){

        try {
            val outputStreamWriter = OutputStreamWriter(
                openFileOutput(
                    "single.json",
                    MODE_PRIVATE
                )
            )
            MySingleton.arrayListOfBundlesOfDocuments?.clear()
            outputStreamWriter.write("")
            outputStreamWriter.close()

    }catch (e:Exception){}
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 1)
            supportFragmentManager.popBackStack()
        else finish()
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == 3) {
                MySingleton.pageclick = 0

                Thread {

                    MySingleton.image = java.util.ArrayList()
                    MySingleton.title = java.util.ArrayList()
                    MySingleton.text = String()
                    MySingleton.image = java.util.ArrayList()
                    MySingleton.day = java.util.ArrayList()
                    MySingleton.time = java.util.ArrayList()
                    MySingleton.status = java.util.ArrayList()



                    MySingleton.countUnsent.set(
                        (MySingleton.countUnsent.get()!!.toInt() + 1).toString()
                    )
                    runOnUiThread {
                        try {
                            findViewById<RecyclerView>(R.id.recycler_view).adapter?.notifyDataSetChanged()
                        }catch (e:java.lang.Exception){

                        }


                    }
                    sendDocuments()

                }.start()

            }

        }


    private fun sendDocuments() = Thread {


        val item = MySingleton.arrayListOfBundlesOfDocuments!![MySingleton.numberOfTheChangedItem]


        for (numberOfStatusField in 0 until item!!.status.size) {
            if (myFunctions.imageRequest(
                    myFunctions.getStringFromBitmap(
                        BitmapFactory.decodeFile(
                            Environment.getExternalStorageDirectory().absolutePath.toString() + "/" + item.numberOfOrderField!!.split(
                                "№"
                            )[1] + "page" + (numberOfStatusField + 1).toString() + ".png"
                        )
                    )!!,
                    item.day[numberOfStatusField]!! + " " + item.time[numberOfStatusField]!![0].toString() + item.time[numberOfStatusField]!![1].toString() + "-" + item.time[numberOfStatusField]!![3].toString() + item.time[numberOfStatusField]!![4].toString(),
                    item.fullInformation!!,
                    sharedPreferencesAddress,
                    sharedPreferencesUser
                ) == "true"
            ) {
                item.status[numberOfStatusField] = "yes"
            }
        }
        var countOfSent = 0
        for (status in  item.status) {
            if (status == "yes")
                countOfSent++
        }
        if (countOfSent == item.status.size)
            MySingleton.countUnsent.set(
                (MySingleton.countUnsent.get()!!.toInt() - 1).toString()
            )

        runOnUiThread {

            try {
                findViewById<RecyclerView>(R.id.recycler_view).adapter?.notifyDataSetChanged()
            }catch (e:java.lang.Exception){

            }
        }

        try {
            myFunctions.saveJson()
        } catch (e: Exception) {
            Log.d("MyLog", e.toString())
        }

    }.start()

    private fun isMyServiceRunning(myClass: Class<MyService>): Boolean {

        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager


        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (myClass.name.equals(service.service.className)) {

                return true

            }

        }
        return false
    }

    private var observableChangeCallback = object : Observable.OnPropertyChangedCallback() {

        override fun onPropertyChanged(observable: Observable, i: Int) {
            if (MySingleton.countUnsent.get() == "0")
                runOnUiThread {
                    findViewById<View>(R.id.counter_unsent).visibility = View.GONE
                }
            else if (MySingleton.countUnsent.get()!!.toInt() > 0)
                runOnUiThread {
                    findViewById<View>(R.id.counter_unsent).visibility = View.VISIBLE
                }
        }
    }

    override fun onStop() {

        Thread {

            if (!isMyServiceRunning(MyService::class.java) && !MySingleton.applicationIsActive && myFunctions.notAllSent()) {
                startService(Intent(this, MyService::class.java))
            }

        }.start()
        super.onStop()
    }



    override fun onPause() {
        super.onPause()
        MySingleton.applicationIsActive = false
    }

    override fun onResume() {
        super.onResume()
        MySingleton.currentOrderNumber = "0"
        MySingleton.dontGoOut = 0
        try {
            Thread {
                myFunctions.saveJson()
            }.start()
            findViewById<RecyclerView>(R.id.recycler_view).adapter?.notifyDataSetChanged()


        } catch (e: Exception) {
            Log.d("MyLog", e.toString())
        }

        MySingleton.applicationIsActive = true
        if (isMyServiceRunning(MyService::class.java)) {
            stopService(Intent(this, MyService::class.java))
        }
    }

}







