package com.example.reactivearch.data.api_response

import com.google.gson.annotations.SerializedName

data class GeoBounds(
    @SerializedName("circle") val circle: Circle
)