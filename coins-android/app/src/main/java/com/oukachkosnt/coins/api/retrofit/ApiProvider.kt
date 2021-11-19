package com.oukachkosnt.coins.api.retrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    private const val host = "http://toficoserver.com/api/"

    private fun getRetrofit(baseUrl: String = host)
        = Retrofit.Builder()
              .baseUrl(baseUrl)
              .addConverterFactory(GsonConverterFactory.create())
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .build()


    val newsApi: NewsApi by lazy { getRetrofit().create(NewsApi::class.java) }
}