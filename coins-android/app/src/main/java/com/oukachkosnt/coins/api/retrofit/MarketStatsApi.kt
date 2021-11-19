package com.oukachkosnt.coins.api.retrofit

import com.oukachkosnt.coins.data.api.CapShareApiData
import com.oukachkosnt.coins.data.api.MarketStatsApiData
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface MarketStatsApi {
    @GET("market-stats/global")
    fun getGlobalStats(): Single<MarketStatsApiData>

    @GET("market-stats/capshare/current/5")
    fun getTopCoinsCapShare(): Single<List<CapShareApiData>>

    // period could be all, `24h`, month, year
    @GET("market-stats/capshare/history/{period}")
    fun getMarketCapShareHistory(@Path("period") period: String): Single<Map<String, List<List<String>>>>

    // period could be all, `24h`, month, year
    @GET("market-stats/capshare/history/zipped/{period}")
    fun getMarketCapShareHistoryZipped(@Path("period") period: String): Single<ResponseBody>
}