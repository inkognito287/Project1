package com.example.qrreader.Pojo

import com.google.gson.annotations.SerializedName

data class Response(

	@field:SerializedName("documents")
	val documents: List<DocumentsItem?>? = null
)

data class DocumentsItem(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("photo")
	val photo: String? = null


)
