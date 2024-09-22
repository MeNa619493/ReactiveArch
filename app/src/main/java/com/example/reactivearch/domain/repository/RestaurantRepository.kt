package com.example.reactivearch.domain.repository

import com.example.reactivearch.core.common.DataState
import com.example.reactivearch.domain.dto.LocationDto
import com.example.reactivearch.domain.dto.RequestDto
import com.example.reactivearch.domain.entity.Restaurant
import io.reactivex.Single

interface RestaurantRepository {
    fun getRestaurant(requestDto: RequestDto) : Single<DataState<List<Restaurant>>>
}