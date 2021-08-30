package com.example.qrreader.activities


//import com.example.qrreader.service.MyService
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


class MainActivity : AppCompatActivity() {


    private lateinit var myBroadcastReceiver: com.example.qrreader.broadcastReceiver.MyBroadcastReceiver
    lateinit var text: String
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferencesAddress: SharedPreferences
    lateinit var sharedPreferencesUser: SharedPreferences

    lateinit var myFragmentTransaction: MyFragmentTransaction
    lateinit var myFunctions: Functions
    //var kring : List<DocumentsItem?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferencesAddress = getSharedPreferences("address", Context.MODE_PRIVATE)
        sharedPreferencesUser = getSharedPreferences("user", Context.MODE_PRIVATE)

        MySingleton.arrayList = ArrayList()
        MySingleton.countUnsent = ObservableField()
        findViewById<View>(R.id.counter_unsent).visibility = View.GONE

        MySingleton.image = java.util.ArrayList()
        MySingleton.title = java.util.ArrayList()
        MySingleton.text = String()
        MySingleton.image = java.util.ArrayList()
        MySingleton.day = java.util.ArrayList()
        MySingleton.time = java.util.ArrayList()
        MySingleton.status = java.util.ArrayList()


        MySingleton.countUnsent.addOnPropertyChangedCallback(snackbarCallback)
        fun isExternalPermissionGranted(): Boolean {
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

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
//        override fun onRequestPermissionsResult(
//            requestCode: Int,
//            permissions: Array<String>,
//            grantResults: IntArray
//        ) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//            if( isExternalPermissionGranted()){}
//
//        }


        MySingleton.countActivity = 1
        myFunctions = Functions(applicationContext)
        //MySingleton.countUnsent.set(0.toString())
        var arrayOfDocumentsItem = ArrayList<ItemForHistory>()
        binding.progressBarMainActivity.visibility = View.VISIBLE
        Thread() {
            MySingleton.arrayList = ArrayList()
            val gson = Gson()

            var text = myFunctions.readFromFile()

            if (text != "" && text != "ERROR") {

                // text=text.substring(1)
                Log.d("MyLog", text)

                try {
                    val result =
                        gson.fromJson(text, com.example.qrreader.pojo.Response2::class.java)
//                       Log.d("MyLog", result.response2!![0]!!.documentFormatField!![0].toString())

                    for (element in result.response2!!)
                        MySingleton.arrayList!!.add(
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
            Log.d("MyLog",e.toString())
                }
                runOnUiThread {

                    var count = 0
                    try {


                        for (element in MySingleton.arrayList!!)
                            if (element!!.status[0] == "no")
                                count++

                    } catch (e: Exception) {
                    }
//                    if (count != 0)
//                        runOnUiThread {
//                            findViewById<View>(R.id.counter_unsent).visibility = View.VISIBLE
//                        }
                    MySingleton.countUnsent.set(count.toString())
//runOnUiThread {
                    //  findViewById<View>(R.id.counter_unsent).visibility = View.VISIBLE
                    binding.count = MySingleton.countUnsent
                }
                //}


            }
            myFragmentTransaction = MyFragmentTransaction(this)
            myBroadcastReceiver = com.example.qrreader.broadcastReceiver.MyBroadcastReceiver()
            //val shardPreference = getSharedPreferences("user", Context.MODE_PRIVATE)
            runOnUiThread() {
                myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())

                binding.progressBarMainActivity.visibility = View.GONE
            }

            val filter = IntentFilter().apply {
                addAction("android.net.conn.CONNECTIVITY_CHANGE")
                addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            }
            this.registerReceiver(myBroadcastReceiver, filter)

        }.start()


        // request()


    }

    fun history(v: View) {
        myFragmentTransaction.fragmentTransactionReplace(HistoryFragment())
    }

    fun camera(v: View) {
        // MySingleton.flag=false
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


    fun data(v: View) {
        val data = DataFragment()
        myFragmentTransaction.fragmentTransactionReplace(data)
    }

    fun clearHistory(v: View) {

        MySingleton.countUnsent.set("0")
        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("Очистка истории")
            .setMessage("Вы уверены, что хотите очистить историю? Неотправленные данные будут удалены")
            .setIcon(R.drawable.clear_history)
            .setPositiveButton("Ок") { dialog, _ ->
                dialog.cancel()
                myAdapterUpdate = myAdapter
                if (myAdapter != null) {


                    myAdapterUpdate?.clear()

                }
                try {


                    val dir =
                        File(Environment.getExternalStorageDirectory().absolutePath)
                    if (dir.isDirectory()) {
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


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 1)
            supportFragmentManager.popBackStack()
        else finish()
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->


            if (result.resultCode == 1) {

            }
            if (result.resultCode == 4) {
//                MySingleton.completedPages.clear()
//                binding.button.isClickable = true
//                for (i in 0 until MySingleton.arrayList!![0]!!.status.size)
//                    MySingleton.arrayList!![0]!!.status[i] = "uncompleted"
//                MySingleton.image = java.util.ArrayList()
//                MySingleton.title = java.util.ArrayList()
//                MySingleton.text = String()
//                MySingleton.image = java.util.ArrayList()
//                MySingleton.day = java.util.ArrayList()
//                MySingleton.time = java.util.ArrayList()
//                MySingleton.status = java.util.ArrayList()

 //               myAdapterUpdate = myAdapter
   //             myAdapterUpdate.update()
//                try {
//                    myFunctions.saveJson()
//                } catch (e: Exception) {
//                    Log.d("MyLog", e.toString())
//                }

            }
            if (result.resultCode == 3) {
                MySingleton.pageclick = 0
                binding.button.isClickable = false

                Thread() {

                    // myFunctions.saveBitmaps(MySingleton.arrayList!![0]!!.image!!, MySingleton.arrayList!![0]!!.numberOfOrderField)
                    MySingleton.image = java.util.ArrayList()
                    MySingleton.title = java.util.ArrayList()
                    MySingleton.text = String()
                    MySingleton.image = java.util.ArrayList()
                    MySingleton.day = java.util.ArrayList()
                    MySingleton.time = java.util.ArrayList()
                    MySingleton.status = java.util.ArrayList()


                    //  MySingleton.itemForHistory = null


                    MySingleton.countUnsent.set(
                        (MySingleton.countUnsent.get()!!.toInt() + 1).toString()
                    )
                    runOnUiThread {

                        //myAdapter.notifyDataSetChanged()
                        myAdapterUpdate.update()
                        binding.button.isClickable = true

                    }



                    deserialize()

                    runOnUiThread {

                    }

                }.start()

            }

        }


    private fun deserialize() = Thread {


        val item = MySingleton.arrayList!![MySingleton.numberOfTheChangedItem]


        for (x in 0..item!!.status.size - 1) {
          //  if (item.status[x] == "no")
                if (myFunctions.imageRequest(
                        myFunctions.getStringFromBitmap(
                            BitmapFactory.decodeFile(
                                Environment.getExternalStorageDirectory().absolutePath.toString() + "/" + item!!.numberOfOrderField!!.split(
                                    "№"
                                )[1] + "page" + (x + 1).toString() + ".png"
                            )
                        )!!,
                        item.day[x]!! + " " + item.time!![x]!![0].toString() + item.time!![x]!![1].toString() + "-" + item.time!![x]!![3].toString() + item.time!![x]!![4].toString(),
                        item.fullInformation!!,
                        sharedPreferencesAddress,
                        sharedPreferencesUser
                    ) == "true"
                ) {
                    item.status[x] = "yes"


                }
        }
        var countOfSended=0
        for (x in 0..item!!.status.size - 1) {
            if (item.status[x]=="yes")
            countOfSended++
        }
        if (countOfSended==item.status.size)
            MySingleton.countUnsent.set(
                (MySingleton.countUnsent.get()!!.toInt() - 1).toString()
            )

        myAdapterUpdate = myAdapter
        runOnUiThread {

            myAdapterUpdate.update()
        }
        //myAdapterUpdate = myAdapter
        runOnUiThread() {

            //binding.button.isClickable = true
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

    var snackbarCallback = object : Observable.OnPropertyChangedCallback() {

        override fun onPropertyChanged(observable: Observable, i: Int) {
            Log.d("MyLog", observable.toString() + " " + i)
            if (MySingleton.countUnsent.get() == "0")
                runOnUiThread {
                    findViewById<View>(R.id.counter_unsent).visibility = View.GONE
                }
            else if(MySingleton.countUnsent.get()!!.toInt()>0)
                runOnUiThread {
                    findViewById<View>(R.id.counter_unsent).visibility = View.VISIBLE
                }
        }
    }

    override fun onStop() {

        Thread() {

            if (!isMyServiceRunning(MyService::class.java) && !MySingleton.applicationIsActive) {
                startService(Intent(this, MyService::class.java))
            }

        }.start()
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        MySingleton.applicationIsActive = false
        Log.d("MyLog", "Main is active=" + MySingleton.applicationIsActive)
    }

    override fun onResume() {
        super.onResume()

        try {


            myAdapterUpdate.update()
            Thread() {
                myFunctions.saveJson()
            }.start()
        } catch (e: Exception) {
            Log.d("MyLog",e.toString())
        }

        //myAdapterUpdate.update()
        MySingleton.applicationIsActive = true
        Log.d("MyLog", "Main is active=" + MySingleton.applicationIsActive)
        if (isMyServiceRunning(MyService::class.java)) {
            stopService(Intent(this, MyService::class.java))
        }
    }


}







