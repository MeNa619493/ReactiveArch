package com.example.reactivearch.data.api

import com.example.reactivearch.data.api_response.RestaurantsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FoursquareAPI {
    @GET("v3/places/search")
    fun getRestaurants(@Query("ll",encoded = true) ll:String) : Single<RestaurantsResponse>
}