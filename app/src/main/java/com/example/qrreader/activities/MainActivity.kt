package com.example.qrreader.activities


import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.qrreader.Fragment.*
import com.example.qrreader.MyFragmentTransaction
import com.example.qrreader.R
import com.google.zxing.integration.android.IntentIntegrator


class MainActivity : AppCompatActivity() {

    lateinit var text: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        fragmentTransactionReplace(HistoryFragment())

    }

    fun history(v: View) {
        fragmentTransactionReplace(HistoryFragment())
    }

    fun camera(v: View) {
        val intent = Intent(this, ImageActivity::class.java)
        startActivity(intent)
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

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {

                val bundle = Bundle()
                bundle.putString("path", result.barcodeImagePath)
                bundle.putString("code", result.contents)
                val imageFragment = ImageFragment()
                imageFragment.setArguments(bundle)
                fragmentTransactionReplace(imageFragment)

            }

        }
        if (resultCode == 1) {
            if (data?.getIntExtra("fragment", 1) == 1)
                fragmentTransactionReplace(HistoryFragment())
            else if (data?.getIntExtra("fragment", 1) == 2)
                fragmentTransactionReplace(SettingFragment())
        }
        if (resultCode == 28) {
            fragmentTransactionReplace(HistoryFragment())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        applicationContext.cacheDir.deleteRecursively()
        super.onDestroy()

    }
}




