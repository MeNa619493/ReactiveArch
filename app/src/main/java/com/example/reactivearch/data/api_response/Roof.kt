package com.example.reactivearch.data.api_response

import com.google.gson.annotations.SerializedName

data class Roof(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)