package com.example.qrreader.Pojo

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("documents")
	val documents: List<DocumentsItem?>? = null
)

data class DocumentsItem(

	@field:SerializedName("date")
	var date: String? = null,

	@field:SerializedName("code")
	var code: String? = null,

	@field:SerializedName("photo")
	var photo: String? = null,

	@field:SerializedName("day")
	var day: String? = null,

	@field:SerializedName("time")
	var time: String? = null,

	@field:SerializedName("status")
	var status: String? = null

)
