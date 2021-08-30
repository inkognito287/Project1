package com.example.qrreader.pojo

import com.google.gson.annotations.SerializedName

data class Response2(

	@field:SerializedName("Response2")
	val response2: List<Response2Item?>? = null
)

data class Response2Item(

//	@field:SerializedName("image")
//	val image: List<ImageItem?>? = null,

	@field:SerializedName("numberOfOrderField")
	val numberOfOrderField: String? = null,

	@field:SerializedName("stringImage")
	val stringImage: List<String?>? = null,

	@field:SerializedName("time")
	val time: List<String?>? = null,

	@field:SerializedName("day")
	val day: List<String?>? = null,

	@field:SerializedName("documentFormatField")
	val documentFormatField: List<String?>? = null,

	@field:SerializedName("fullInformation")
	val fullInformation: String? = null,

	@field:SerializedName("status")
	val status: List<String?>? = null
)

data class ImageItem(

	@field:SerializedName("mHeight")
	val mHeight: Int? = null,

	@field:SerializedName("mWidth")
	val mWidth: Int? = null,

	@field:SerializedName("mNativePtr")
	val mNativePtr: Long? = null,

	@field:SerializedName("mIsMutable")
	val mIsMutable: Boolean? = null
)
