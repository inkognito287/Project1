package com.example.qrreader.Pojo

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("kek")
	val kek: Kek? = null
)

data class Kek(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("photo")
	val photo: String? = null
)
