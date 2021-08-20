package com.example.qrreader.activities


import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import com.example.qrreader.fragment.SettingFragment
import com.example.qrreader.fragment.*
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.R
import com.example.qrreader.databinding.ActivityMainBinding
import com.example.qrreader.Functions
import com.example.qrreader.model.ItemForHistory
import com.example.qrreader.service.MyService
import com.example.qrreader.singletones.MySingleton
import com.google.gson.Gson
import java.lang.Exception


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
        sharedPreferencesUser = getSharedPreferences("user",Context.MODE_PRIVATE)

        MySingleton.countUnsent = ObservableField()
        findViewById<View>(R.id.counter_unsent).visibility = View.GONE

        MySingleton.countActivity = 1
        myFunctions = Functions(applicationContext)

        var arrayOfDocumentsItem = ArrayList<ItemForHistory>()
        binding.progressBarMainActivity.visibility = View.VISIBLE
        Thread() {
            MySingleton.arrayList = ArrayList()
            val gson = Gson()
            val text = myFunctions.readFromFile()
            if (text != "") {
                val result = gson.fromJson(text, com.example.qrreader.pojo.Response::class.java)

                for (element in result.documents!!)
                    arrayOfDocumentsItem.add(
                        ItemForHistory(
                            element!!.documentFormatField.toString(),
                            element.numberOfOrderField.toString(),
                            element.photo.toString(),
                            myFunctions.getBitmapFromString(element.photo.toString())!!,
                            element.day.toString(),
                            element.time.toString(),
                            element.status.toString(),
                            element.fullInformation.toString()
                        )
                    )

                MySingleton.arrayList!!.addAll(arrayOfDocumentsItem)

                    var count = 0
                    for (element in MySingleton.arrayList!!)
                        if (element.status=="no")
                            count++

                if (count!=0)
                    runOnUiThread {
                        findViewById<View>(R.id.counter_unsent).visibility = View.VISIBLE
                    }
                MySingleton.countUnsent.set(count.toString())
runOnUiThread {
    binding.count = MySingleton.countUnsent
}


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
            MySingleton.scanActivityExistFlag = false
            if (result.resultCode == 28) {

                binding.button.isClickable = false
                Thread() {
                    MySingleton.countUnsent.set ((MySingleton.countUnsent.get()!!.toInt()+1).toString())
                    runOnUiThread {


                        myAdapter?.notifyDataSetChanged()
                    }

                    MySingleton.arrayList!![0].stringImage =
                        myFunctions.getStringFromBitmap(MySingleton.arrayList!![0].image).toString()
                    runOnUiThread {
                        findViewById<View>(R.id.counter_unsent).visibility = View.VISIBLE
                    }
                    deserialize()

                    runOnUiThread {
                        binding.button.isClickable = true
                    }
                }.start()

            }

        }


    private fun deserialize() {


        Thread {


            val first = MySingleton.arrayList!![0]



            if (first.status == "no")
                if (myFunctions.imageRequest(
                        first.stringImage!!,
                        first.day!! + " " + first.time!![0].toString() + first.time!![1].toString() + "-" + first.time!![3].toString() + first.time!![4].toString(),
                        first.fullInformation,
                        sharedPreferencesAddress,
                        sharedPreferencesUser
                    ) == "true"
                ) {
                    MySingleton.countUnsent.set ((MySingleton.countUnsent.get()!!.toInt()-1).toString())
                    if(MySingleton.countUnsent.get()=="0")
                        runOnUiThread {
                            //(binding.counterUnsent as View).visibility =View.GONE
                            findViewById<View>(R.id.counter_unsent).visibility = View.GONE
                        }
                    MySingleton.arrayList!![0].status = "yes"

                    myAdapterUpdate = myAdapter
                    runOnUiThread() {
                        myAdapterUpdate.update()
                    }
                }
           try{ myFunctions.saveJson()}catch (e:Exception){}
            binding.button.isClickable = true


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

        var s = 0
        if (MySingleton.arrayList != null) {

            for (x in 0 until MySingleton.arrayList!!.size)
                if (MySingleton.arrayList!![x].status == "no")
                    s++
        }
        if (s > 0)

            return true

        return false

    }

    override fun onStop() {
        super.onStop()

        Log.d("life", "Main act Stop MySingleton.flag" + MySingleton.mainActivityExistFlag.toString())
        Thread() {
            if (!isMyServiceRunning(MyService::class.java) && MySingleton.mainActivityExistFlag && MySingleton.countActivity == 1) {
                startService(Intent(this, MyService::class.java))
            }
            MySingleton.scanActivityExistFlag = true
            //  if( MySingleton.flag && MySingleton.countActivity == 1)

        }.start()
    }

    override fun onResume() {
        super.onResume()
        if(MySingleton.countUnsent.get()=="0")
        findViewById<View>(R.id.counter_unsent).visibility = View.GONE
        Log.d("life", "resume")
        if (isMyServiceRunning(MyService::class.java)) {
           // stopService(Intent(this, MyService::class.java))
        }
    }


}







