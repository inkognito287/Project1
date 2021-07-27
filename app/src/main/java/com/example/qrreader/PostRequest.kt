package com.example.qrreader

import java.net.HttpURLConnection
import java.net.URL

class PostRequest() {

    var string = ""
    fun request( url: URL,  password: String,  name: String): String {

        val url =
            URL("$url/Home/test?Name=$name&Password=$password")

        Thread {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"  // optional default is GET
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        string = line
                    }


                }
            }

        }.start()
        return string
    }
}