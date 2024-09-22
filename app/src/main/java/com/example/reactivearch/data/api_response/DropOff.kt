package com.example.reactivearch.data.api_response

import com.google.gson.annotations.SerializedName

data class DropOff(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)