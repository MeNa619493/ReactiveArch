package com.example.reactivearch.data.api_response

import com.google.gson.annotations.SerializedName

data class Circle(
    @SerializedName("center") val center: Center,
    @SerializedName("radius") val radius: Int
)