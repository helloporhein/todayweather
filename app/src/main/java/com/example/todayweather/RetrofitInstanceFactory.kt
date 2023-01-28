package com.example.todayweather

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstanceFactory {

    private var retrofit : Retrofit? = null

    fun instance() : Retrofit {
        if (retrofit == null) {
            val okHttpClientBuilder = OkHttpClient.Builder()

            val appIdInterceptor = AppIdInterceptor()

            okHttpClientBuilder.addInterceptor(appIdInterceptor)

            val unitInterceptor = UnitInterceptor()

            okHttpClientBuilder.addInterceptor(unitInterceptor)

            retrofit = Retrofit.Builder()
                    .baseUrl( "https://api.openweathermap.org/data/2.5/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .client(okHttpClientBuilder.build())
                    .build()

        }
        return retrofit!!
    }
}