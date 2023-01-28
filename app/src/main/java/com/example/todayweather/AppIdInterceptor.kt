package com.example.todayweather

import android.os.Parcel
import android.os.Parcelable
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response


class AppIdInterceptor : Interceptor {

    companion object {
        private const val API_KEY = "0bb3cbe4d192497f38024579d64516b2"
    }

    override fun intercept(chain: Chain): Response{

        val url = chain.request().url

        val newUrl = url.newBuilder()
                .addQueryParameter( "appid", API_KEY)
                .build()

        val request = Request.Builder().url(newUrl).build()

        return chain.proceed(request)
    }

}