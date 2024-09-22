package com.example.reactivearch.data.api_response

import com.google.gson.annotations.SerializedName

data class RelatedPlaces(
    @SerializedName("parent") val parent: Parent
)