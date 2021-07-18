package com.example.qrreader


import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.net.Uri.parse
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

import android.view.View
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.zxing.integration.android.IntentIntegrator
import java.net.URI
import java.util.Date.parse
import java.util.logging.Level.parse


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentTransactionReplace(MainFragment())
    }

    fun history(v: View) {
        fragmentTransactionReplace(HistoryFragment())
    }

    fun camera(v: View) {
        fragmentTransactionReplace(MainFragment())
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
            if (result.contents!=null){
                val builder= AlertDialog.Builder(this)
                builder.setMessage(result.contents)
               var path =result.barcodeImagePath
                val imageView=ImageView(this)
                imageView.setImageURI(Uri.parse(path))
                 //setImageResource(resources.getIdentifier(path,"drawable",packageName))
                builder.setTitle("Scanning Result")
                builder.setView(imageView)
                //   builder.setPositiveButton("Scan Again")
                val dialog=builder.create()
                dialog.show()
            }

        }
    }
    private fun takePicture() {


        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, 1)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }

    }

}


