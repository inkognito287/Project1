package com.example.qrreader.Fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.graphics.drawable.toBitmap
import com.example.qrreader.Pojo.Response
import com.example.qrreader.R
import com.example.qrreader.activities.ImageActivity
import com.example.qrreader.databinding.FragmentImageBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class ImageFragment : Fragment() {
    lateinit var imageVew: ImageView
    lateinit var text: String
    lateinit var saveCode: String
    lateinit var saveImage: Bitmap
    lateinit var  binding:FragmentImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(inflater, container, false)
        //binding.imageView7.setImageURI(Uri.parse(arguments?.getString("path")))
        binding.imageFragmentSubmit.setOnClickListener() {
            submitImage()
        }
        binding.imageFragmentBack.setOnClickListener() {
            backImage()
        }
        saveImage = activity?.findViewById<PreviewView>(R.id.textureView)?.bitmap!!
        saveCode = arguments?.getString("code")!!
        imageVew = binding.imageView7
        imageVew.setImageBitmap(saveImage)

        binding.code.text = saveCode
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun writeToFile(jsonData: String?) {
        try {
            val outputStreamWriter = OutputStreamWriter(
                activity?.openFileOutput(
                    "single.json",
                    AppCompatActivity.MODE_PRIVATE
                )
            )
            outputStreamWriter.write(jsonData)

            outputStreamWriter.close()
            println("good")
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readToFile(): String {
        try {
            val reader = BufferedReader(InputStreamReader(activity?.openFileInput("single.json")))
            text = reader.readText()
            reader.close()
            return text
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
            return "ERROR"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createJsonObject(
        photo: String,
        code: String,
        date: String,
        day: String,
        time: String
    ): String? {
        val jsonNowString = readToFile()
        Log.d("MyLog", "ReadFile=" + text)

        val gson = Gson()
        val rootObject = JsonObject()
        val arrayObject = JsonArray()
        try {
            val deserializer = gson.fromJson(jsonNowString, Response::class.java)
            //  Log.d("MyLog", "deserializer =" + deserializer.documents!![0].toString())


            for (i in 0..deserializer.documents!!.size - 1) {

                val childObject = JsonObject()

                childObject.addProperty(
                    "code",
                    deserializer.documents.get(i)?.code
                )
                childObject.addProperty(
                    "date",
                    deserializer.documents.get(i)?.date
                )
                childObject.addProperty(
                    "photo",
                    deserializer.documents.get(i)?.photo
                )
                childObject.addProperty(
                    "day",
                    deserializer.documents.get(i)?.day
                )
                childObject.addProperty(
                    "time",
                    deserializer.documents.get(i)?.time
                )
                arrayObject.add(childObject)

            }
        } catch (e: Exception) {
        }
        // создаем главный объект

        val childObject = JsonObject()
        // записываем текст в поле "message"
        childObject.addProperty("code", code)
        childObject.addProperty("date", date)
        childObject.addProperty("photo", photo)
        childObject.addProperty("day", day)
        childObject.addProperty("time", time)
        arrayObject.add(childObject)

        rootObject.add("documents", arrayObject)
//        //rootObject.add("kek",childObject)
//        arrayObject.add(childObject)
//        rootObject.add("documents",arrayObject)

        val json = gson.toJson(rootObject)

        // val data="{\"kek\":{\"photo\":\"\",\"code\":\"ES00003885860000000000ASV0201\"}}"
        //очерний объект в поле "place"


        return json  // генерация json строки
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun submitImage() {
        binding.progressBar.visibility=View.VISIBLE
        Thread() {
            val date = Date()
            var dateFormatTime = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.US);
            var dateFormatDay = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.US);
            var OUTPUT_PATERN_DAY = SimpleDateFormat("dd MMMM");
            var OUTPUT_PATERN_TIME = SimpleDateFormat("HH:mm");
            var test = dateFormatTime.parse(date.toString())
            var time = OUTPUT_PATERN_TIME.format(test)
            var day = OUTPUT_PATERN_DAY.format(test)

            writeToFile(
                createJsonObject(
                    getStringFromBitmap(saveImage)!!,
                    saveCode,
                    date.toString(),
                    day,
                    time
                )
            )

        //readToFile()
        activity?.setResult(28)
        activity?.finish()
        }.start()
    }

    fun backImage() {
        val bottomSheetBehaviour =
            BottomSheetBehavior.from(activity?.findViewById(R.id.containerBottomSheet)!!)
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        //activity?.findViewById<Button>(R.id.button)?.isClickable=true
    }


}