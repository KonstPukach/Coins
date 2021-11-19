package com.oukachkosnt.coins.api.retrofit

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ChartsApi {

    // period is 24h, week, month, year, all
    @GET("coins/history/{coinId}/{period}")
    fun getCoinPriceHistory(
        @Path("coinId") coinId: String,
        @Path("period") period: String
    ): Single<List<List<String>>>
}