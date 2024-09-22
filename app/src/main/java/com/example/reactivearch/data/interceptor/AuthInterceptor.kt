package com.example.reactivearch.data.interceptor

import android.content.Context
import com.example.reactivearch.R
import com.example.reactivearch.core.common.AppConst.AUTHORIZATION
import com.example.reactivearch.core.common.AppConst.DEFAULT_QUERY
import com.example.reactivearch.core.common.AppConst.QUERY
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val appContext : Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request =  chain.request()
        val originalUrl = request.url
        val modifiedUrl =  originalUrl.newBuilder()
            .addQueryParameter(QUERY, DEFAULT_QUERY)
            .build()

        val modifiedRequest =  request.newBuilder().header(AUTHORIZATION, appContext.getString(R.string.api_key)).url(modifiedUrl).build()
        return chain.proceed(modifiedRequest)
    }
}