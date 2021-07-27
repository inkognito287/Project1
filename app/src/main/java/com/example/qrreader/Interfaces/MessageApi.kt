package com.example.qrreader.Interfaces


import com.example.qrreader.Message
import com.example.qrreader.Pojo.Response
import retrofit2.http.GET

interface MessageApi {
    public interface ServerApi {
        @GET("messages1.json")
        fun messages(): retrofit2.Call<Response>

    }
}