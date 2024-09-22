package com.example.reactivearch.data.repository

import com.example.reactivearch.core.common.DataState
import com.example.reactivearch.data.api.FoursquareAPI
import com.example.reactivearch.data.cache.InMemoryCache
import com.example.reactivearch.domain.dto.LocationDto
import com.example.reactivearch.domain.dto.RequestDto
import com.example.reactivearch.domain.entity.Restaurant
import com.example.reactivearch.domain.repository.RestaurantRepository
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single

class RestaurantRepositoryImpl (private val api: FoursquareAPI) : RestaurantRepository {
    override fun getRestaurant(requestDto: RequestDto): Single<DataState<List<Restaurant>>> {
        // get cache first
        val cache = InMemoryCache.get()
        val filteredData = ArrayList<Restaurant>()
        cache.forEach{
            val latlng = LatLng(it.latitude ,it.longitude)
            if (requestDto.latLngBounds.contains(latlng))
                filteredData.add(it)
        }

        if (filteredData.isNotEmpty())
            return  Single.just(DataState.Success(filteredData))

        return  api.getRestaurants("${requestDto.latLng.latitude},${requestDto.latLng.longitude}")
            .map {
                val restList = ArrayList<Restaurant>()
                it.results.forEach {
                        rest ->
                    val newRestaurant =  Restaurant(
                        id = rest.fsqId,
                        name = rest.name,
                        city = rest.location.locality,
                        address = rest.location.country,
                        latitude = rest.geocodes.main.latitude,
                        longitude = rest.geocodes.main.longitude,
                    )
                    restList.add(newRestaurant)
                }
                // add data to in memory cache
                InMemoryCache.add(restList)
                DataState.Success(restList)
            }
    }

}