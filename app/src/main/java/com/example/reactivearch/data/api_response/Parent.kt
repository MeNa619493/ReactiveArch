package com.example.reactivearch.data.api_response

import com.google.gson.annotations.SerializedName

data class Parent(
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("fsq_id") val fsqId: String,
    @SerializedName("name") val name: String
)