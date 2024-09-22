package com.example.reactivearch.data.cache

import com.example.reactivearch.domain.entity.Restaurant


object InMemoryCache {
    private val cache = ArrayList<Restaurant>()

    fun get() = cache

    fun add(restaurants: List<Restaurant>) {
        cache.addAll(restaurants)
    }
}