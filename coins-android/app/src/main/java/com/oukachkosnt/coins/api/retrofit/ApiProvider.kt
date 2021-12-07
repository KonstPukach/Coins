package com.oukachkosnt.coins.api.retrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    private const val host = "http://192.168.43.25:61208/api/"

    private fun getRetrofit(baseUrl: String = host)
        = Retrofit.Builder()
              .baseUrl(baseUrl)
              .addConverterFactory(GsonConverterFactory.create())
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .build()

    val cryptoCoinsApi: CryptoCoinsApi by lazy { getRetrofit().create(CryptoCoinsApi::class.java) }

    val newsApi: NewsApi by lazy { getRetrofit().create(NewsApi::class.java) }

    val marketStatsApi: MarketStatsApi by lazy { getRetrofit().create(MarketStatsApi::class.java) }

    val chartsApi: ChartsApi by lazy { getRetrofit().create(ChartsApi::class.java) }

    val exchangeRateApi: ExchangeRateApi by lazy { getRetrofit().create(ExchangeRateApi::class.java) }

    val alertsApi: AlertsApi by lazy { getRetrofit().create(AlertsApi::class.java) }
}