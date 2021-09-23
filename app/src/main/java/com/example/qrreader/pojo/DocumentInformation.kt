package com.example.qrreader.pojo

import com.google.gson.annotations.SerializedName

data class DocumentInformation(

	@field:SerializedName("pageNumber")
	val pageNumber: Int? = null,

	@field:SerializedName("hasNextPage")
	val hasNextPage: Boolean? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("pageSize")
	val pageSize: Int? = null,

	@field:SerializedName("items")
	val items: List<ItemsItem?>? = null
)

data class ManagerId(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("value")
	val value: String? = null
)

data class ItemsItem(

	@field:SerializedName("note")
	val note: String? = null,

	@field:SerializedName("requiresResponse")
	val requiresResponse: Boolean? = null,

	@field:SerializedName("statusColor")
	val statusColor: String? = null,

	@field:SerializedName("managerName")
	val managerName: String? = null,

	@field:SerializedName("createdOn")
	val createdOn: String? = null,

	@field:SerializedName("productName")
	val productName: String? = null,

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("begunManifacturing")
	val begunManifacturing: Any? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("budget")
	val budget: Budget? = null,

	@field:SerializedName("stageId")
	val stageId: String? = null,

	@field:SerializedName("lastContragentActivity")
	val lastContragentActivity: String? = null,

	@field:SerializedName("contragentEmailAddress")
	val contragentEmailAddress: String? = null,

	@field:SerializedName("contragentName")
	val contragentName: String? = null,

	@field:SerializedName("isNewOrOfferRejectedStatus")
	val isNewOrOfferRejectedStatus: Boolean? = null,

	@field:SerializedName("managerId")
	val managerId: ManagerId? = null,

	@field:SerializedName("statusId")
	val statusId: String? = null,

	@field:SerializedName("stage")
	val stage: String? = null,

	@field:SerializedName("stageColor")
	val stageColor: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("isSpam")
	val isSpam: Boolean? = null,

	@field:SerializedName("isExpired")
	val isExpired: Boolean? = null,

	@field:SerializedName("contragentPhoneNumber")
	val contragentPhoneNumber: Any? = null,

	@field:SerializedName("isFinalStatus")
	val isFinalStatus: Boolean? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Budget(

	@field:SerializedName("amount")
	val amount: Double? = null,

	@field:SerializedName("currency")
	val currency: Any? = null
)
