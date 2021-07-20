package com.example.qrreader


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.zxing.integration.android.IntentIntegrator
import java.io.File
import java.io.FileOutputStream
import java.util.*


const val APP_PREFERENCES = "mysettings"
const val APP_PREFERENCES_Image = "Image" // имя кота
const val APP_PREFERENCES_Code = "Code" // возраст кота


class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentTransactionReplace(HistoryFragment())
        sharedPreferences=getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)
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

    fun fragmentTransactionReplace(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount != 0)
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        else super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(result.contents)
                val path = result.barcodeImagePath
                val imageView = ImageView(this)
                imageView.setImageURI(Uri.parse(path))
                builder.setTitle("Scanning Result")
                builder.setView(imageView)
                val dialog = builder.create()
                dialog.show()
                val editor = sharedPreferences.edit()
                val date = Date()
                editor.putString(date.toString(), path)
                editor.apply()

            }

        }
    }

}




