package com.example.qrreader


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.qrreader.Pojo.Response
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.zxing.integration.android.IntentIntegrator
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


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
        IntentIntegrator(this).setOrientationLocked(false)
            .setCaptureActivity(CustomScannerActivity::class.java).setBarcodeImageEnabled(true)
            .setBarcodeImageEnabled(true).setPrompt("").setOrientationLocked(false)
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .initiateScan()

    }

    fun setting(v: View) {

        fragmentTransactionReplace(SettingFragment())
    }

    fun finish(v: View) {
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

    fun historBack(v:View){
        finish()
    }

    fun fragmentTransactionReplace(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 0)
            supportFragmentManager.popBackStack()
        else super.onBackPressed()
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
        if (resultCode==1){
            if (data?.getIntExtra("fragment",1)==1)
            fragmentTransactionReplace(HistoryFragment())
            else if (data?.getIntExtra("fragment",1)==2)
                fragmentTransactionReplace(SettingFragment())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        applicationContext.cacheDir.deleteRecursively()
        super.onDestroy()

    }
}




