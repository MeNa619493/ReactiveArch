package com.example.reactivearch.core.di

import android.content.Context
import com.example.reactivearch.core.common.AppConst.BASE_URL
import com.example.reactivearch.core.common.AppConst.TIME_OUT_VALUE
import com.example.reactivearch.data.api.FoursquareAPI
import com.example.reactivearch.data.interceptor.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import java.util.concurrent.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Singleton
    @Provides
    fun providesLoggingInterceptor() : HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level= HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Singleton
    @Provides
    fun providesAuthInterceptor(@ApplicationContext applicationContext: Context):AuthInterceptor{
        return  AuthInterceptor(applicationContext)
    }

    @Singleton
    @Provides
    fun providesOkhttpClient(authInterceptor: AuthInterceptor , loggingInterceptor: HttpLoggingInterceptor): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIME_OUT_VALUE,TimeUnit.SECONDS)
            .readTimeout(TIME_OUT_VALUE,TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT_VALUE,TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun providesRetrofit(okHttpClient: OkHttpClient) : Retrofit.Builder{
        return Retrofit.Builder()
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Singleton
    @Provides
    fun providesFoursquareAPI(retrofit: Retrofit.Builder): FoursquareAPI {
        return retrofit.baseUrl(BASE_URL).build().create(FoursquareAPI::class.java)
    }
}