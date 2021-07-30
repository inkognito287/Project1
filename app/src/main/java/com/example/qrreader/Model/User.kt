package com.example.qrreader.Model

import com.google.gson.annotations.SerializedName

data class User (

   var name: String,
    var password: String,
    @SerializedName("body")
    var text:String


)