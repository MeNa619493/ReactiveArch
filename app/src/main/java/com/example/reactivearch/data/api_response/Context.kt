package com.example.reactivearch.data.api_response

import com.google.gson.annotations.SerializedName

data class Context(
    @SerializedName("geo_bounds") val geoBounds: GeoBounds
)