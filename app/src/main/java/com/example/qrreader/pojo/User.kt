package com.example.qrreader.pojo

import com.google.gson.annotations.SerializedName

data class User(
    @field:SerializedName("access_token")
    var access_token:String? = null

)