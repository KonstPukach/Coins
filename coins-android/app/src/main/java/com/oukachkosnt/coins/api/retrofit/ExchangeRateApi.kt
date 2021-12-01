package com.oukachkosnt.coins.api.retrofit

import com.oukachkosnt.coins.data.api.ExchangeRatesApiData
import io.reactivex.Single
import retrofit2.http.GET

interface ExchangeRateApi {

    @GET("exchange-rates/100")
    fun getUsdBasedExchangeRates(): Single<ExchangeRatesApiData>
}