package com.example.reactivearch.core.di

import com.example.reactivearch.data.api.FoursquareAPI
import com.example.reactivearch.data.repository.RestaurantRepositoryImpl
import com.example.reactivearch.domain.repository.RestaurantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Singleton
    @Provides
    fun providesRestaurantRepository(api: FoursquareAPI): RestaurantRepository {
        return RestaurantRepositoryImpl(api)
    }
}