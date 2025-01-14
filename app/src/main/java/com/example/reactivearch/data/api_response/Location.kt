package com.example.reactivearch.data.api_response

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("address") val address: String,
    @SerializedName("address_extended") val addressExtended: String,
    @SerializedName("census_block") val censusBlock: String,
    @SerializedName("country") val country: String,
    @SerializedName("cross_street") val crossStreet: String,
    @SerializedName("dma") val dma: String,
    @SerializedName("formatted_address") val formattedAddress: String,
    @SerializedName("locality") val locality: String,
    @SerializedName("postcode") val postcode: String,
    @SerializedName("region") val region: String
)