package com.oukachkosnt.coins.api.retrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {
    private const val host = "http://10.0.2.2:61208/api/"

    private const val coinMarketCapHost = "https://api.coinmarketcap.com/"

    private fun getRetrofit(baseUrl: String = host)
        = Retrofit.Builder()
              .baseUrl(baseUrl)
              .addConverterFactory(GsonConverterFactory.create())
              .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .build()

    val coinMarketCapApi: CoinMarketCapApi by lazy { getRetrofit(coinMarketCapHost).create(CoinMarketCapApi::class.java) }

    val cryptoCoinsApi: CryptoCoinsApi by lazy { getRetrofit().create(CryptoCoinsApi::class.java) }

    val newsApi: NewsApi by lazy { getRetrofit().create(NewsApi::class.java) }

    val marketStatsApi: MarketStatsApi by lazy { getRetrofit().create(MarketStatsApi::class.java) }

    val chartsApi: ChartsApi by lazy { getRetrofit().create(ChartsApi::class.java) }

    val exchangeRateApi: ExchangeRateApi by lazy { getRetrofit().create(ExchangeRateApi::class.java) }
}