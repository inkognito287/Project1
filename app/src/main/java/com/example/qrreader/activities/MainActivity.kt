package com.example.qrreader.activities


import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.qrreader.MyBroadcastReceiver
import com.example.qrreader.fragment.*
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.R
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class MainActivity : AppCompatActivity() {


    var myBroadcastReceiver=com.example.qrreader.broadcastReceiver.MyBroadcastReceiver()
    lateinit var text: String
    lateinit var historyFragment: HistoryFragment
    //lateinit var updateAdapter:UpdateAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        historyFragment= HistoryFragment()
        request()
        var filter=IntentFilter().apply {
            addAction("android.net.conn.CONNECTIVITY_CHANGE")
        addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        }
        this.registerReceiver(myBroadcastReceiver,filter)
       findViewById<ConstraintLayout>(R.id.mainConstraint).isEnabled=false
        fragmentTransactionReplace(historyFragment)

    }

    fun history(v: View) {
        fragmentTransactionReplace(historyFragment)
    }

    fun camera(v: View) {
        val intent = Intent(this, BarcodeScanActivity::class.java)
        startActivityForResult(intent,8)
    }

    fun setting(v: View) {

        fragmentTransactionReplace(SettingFragment())
    }

    fun finish(v: View) {
        var sharedPreferences=getSharedPreferences("user",Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.clear().apply()
        var intent=Intent(this,Authorization::class.java)
        startActivity(intent)
        finish()
    }

    fun back(v: View) {
        onBackPressed()
    }

    fun secure(v: View) {
        val secure = SecureFragment()
        fragmentTransactionReplace(secure)
    }

    fun data(v: View) {
        val data = DataFragment()
        fragmentTransactionReplace(data)
    }

    fun historBack(v: View) {
        finish()
    }

    fun fragmentTransactionReplace(fragment: Fragment) {

        var myFragmentTransaction=MyFragmentTransaction(this)
        myFragmentTransaction.fragmentTransactionReplace(fragment)

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 1)
            supportFragmentManager.popBackStack()
        else finish()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            if (data?.getIntExtra("fragment", 1) == 1)
                fragmentTransactionReplace(historyFragment)
            else if (data?.getIntExtra("fragment", 1) == 2)
                fragmentTransactionReplace(SettingFragment())
        }
        if (resultCode == 28) {



//            var z=HistoryFragment()
//                  z.update()
    supportFragmentManager.beginTransaction().detach(historyFragment).commit()
            supportFragmentManager.beginTransaction().attach(historyFragment).commit()
           // fragmentTransactionReplace(SettingFragment())


          //  updateAdapter.update()

        }

    }

    fun request(){
        Thread {
            var token = "rerere"
            var client= OkHttpClient()
            var requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", "Nikita")
                .addFormDataPart("password", "123")
                .build();

            var request = Request.Builder()
                .addHeader("token", token)
                .url("http://86.57.171.246:7777/Account/test")
                .post(requestBody)
                .build();


            try {
                val response: Response = client.newCall(request).execute()
                    runOnUiThread(){
                        Log.d("MyLog",response.body!!.string())


                    }
                // Do something with the response.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()


    }






    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        applicationContext.cacheDir.deleteRecursively()
        super.onDestroy()

    }
}




