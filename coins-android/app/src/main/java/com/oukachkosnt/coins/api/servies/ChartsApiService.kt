package com.oukachkosnt.coins.api.servies

import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import com.google.gson.reflect.TypeToken
import com.oukachkosnt.coins.api.retrofit.ApiProvider
import com.oukachkosnt.coins.data.domain.TimeInterval
import com.oukachkosnt.coins.data.mappers.parseDoublePlotArgs
import com.oukachkosnt.coins.data.mappers.parsePlotArgs
import com.oukachkosnt.coins.utils.unzipResponse

class ChartsApiService {
    companion object {
        private val periodMap = mapOf(
            TimeInterval.DAY      to "24h",
            TimeInterval.WEEK     to "week",
            TimeInterval.MONTH    to "month",
            TimeInterval.YEAR     to "year",
            TimeInterval.ALL_TIME to "all"
        )
    }

    fun getMarketCapShareChartData(timeInterval: TimeInterval): Single<Map<String, List<Pair<Date, Double>>>> {
        return ApiProvider.marketStatsApi
            .getMarketCapShareHistoryZipped(periodMap.getValue(timeInterval))
            .subscribeOn(Schedulers.io())
            .map {
                Gson().fromJson<Map<String, List<List<Double>>>>(
                    it.unzipResponse()
                        .trim('"')
                        .replace("\\", ""),

                    object : TypeToken<Map<String, List<List<Double>>>>() {}.type
                )
            }
            .map { it.mapValues { it.value.parseDoublePlotArgs() } }
            .map { it.mapValues { it.value.filterIndexed { i, _ -> i % 2 == 0 } } }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getCoinPriceChartData(
        coinId: String,
        timeInterval: TimeInterval
    ): Single<List<Pair<Date, Double>>> {
        return ApiProvider.chartsApi
            .getCoinPriceHistory(coinId, periodMap.getValue(timeInterval))
            .subscribeOn(Schedulers.io())
            .map { it.parsePlotArgs() }
            .observeOn(AndroidSchedulers.mainThread())
    }
}