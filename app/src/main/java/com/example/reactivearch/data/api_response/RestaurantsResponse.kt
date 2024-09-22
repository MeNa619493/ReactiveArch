package com.example.reactivearch.data.api_response

import com.google.gson.annotations.SerializedName

data class RestaurantsResponse(
    @SerializedName("context") val context: Context,
    @SerializedName("results") val results: List<Result>
)