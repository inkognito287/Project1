package com.example.qrreader


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.qrreader.Pojo.Response
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.stream.JsonReader
import com.google.zxing.integration.android.IntentIntegrator
import java.io.*
import java.util.*


const val APP_PREFERENCES = "mysettings"
const val APP_PREFERENCES_Image = "Image" // имя кота
const val APP_PREFERENCES_Code = "Code" // возраст кота


class MainActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var text:String
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentTransactionReplace(HistoryFragment())
        sharedPreferences=getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE)
        text=readToFile()
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

    @RequiresApi(Build.VERSION_CODES.O)
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
//getStringFromBitmap(imageView.drawable.toBitmap())!!
                writeToFile(createJsonObject("",result.contents,"11"))
                readToFile()
                deserealization()

            }

        }
    }
    private fun writeToFile(jsonData: JsonObject) {
        try {
            val outputStreamWriter = OutputStreamWriter(openFileOutput("single.json", MODE_PRIVATE))
            outputStreamWriter.write(jsonData.toString())

            outputStreamWriter.close()
            println("good")
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun readToFile():String {
        try {
            val reader=BufferedReader(InputStreamReader(openFileInput("single.json")))
            //findViewById<ImageView>(R.id.imageView3).setImageBitmap(getBitmapFromString(reader.readText()))

            Log.d("MyLog","ReadFile="+reader.readText())
            reader.close()
            text=reader.readLine()
            return reader.readText()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
            return "ERROR"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStringFromBitmap(bitmapPicture: Bitmap): String? {
        val COMPRESSION_QUALITY = 100
        val encodedImage: String
        val byteArrayBitmapStream = ByteArrayOutputStream()
        bitmapPicture.compress(
            Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
            byteArrayBitmapStream
        )
        val b: ByteArray = byteArrayBitmapStream.toByteArray()
        encodedImage = Base64.getEncoder().encodeToString(b)
        return encodedImage
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getBitmapFromString(stringPicture: String): Bitmap? {
        val decodedString: ByteArray = Base64.getDecoder().decode(stringPicture)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun createJsonObject(photo: String, code: String, date: String): JsonObject {




        val rootObject = JsonObject() // создаем главный объект
        val childObject = JsonObject()// создаем объект Place
        childObject.addProperty("photo", photo) // записываем текст в поле "message"
        childObject.addProperty("code", code)
        rootObject.add("kek",childObject)
       // val data="{\"kek\":{\"photo\":\"\",\"code\":\"ES00003885860000000000ASV0201\"}}"
        //очерний объект в поле "place"
        val gson = Gson()

        return rootObject  // генерация json строки
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deserealization()
    {
        val gson=Gson()

        val data="{\"kek\":{\"photo\":\"\",\"code\":\"ES00003885860000000000ASV0201\"}}"
        Log.d("MyLog","data ="+data)
        val kek = gson.fromJson(readToFile(),Response::class.java)
        Log.d("MyLog", kek.kek?.code.toString())

    }
}




