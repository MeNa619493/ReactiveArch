package com.example.reactivearch.core.navigation

import com.example.reactivearch.domain.entity.Restaurant

interface AppNavigator {
    fun navigateTo(screen:Screen, restaurant: Restaurant? = null)
}

enum class Screen{
    MAP, RESTAURANT
}