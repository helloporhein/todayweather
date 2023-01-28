package com.example.todayweather

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response

class UnitInterceptor : Interceptor {

    override fun intercept(chain: Chain): Response {

        val url = chain.request().url

        val newUrl = url.newBuilder()
                .addQueryParameter( "units", "metric")
                .build()

        val request = Request.Builder()
                .url(newUrl)
                .build()

        return chain.proceed(request)

    }


}