package com.oukachkosnt.coins.api.servies

import com.oukachkosnt.coins.api.retrofit.ApiProvider
import com.oukachkosnt.coins.data.domain.CapShareData
import com.oukachkosnt.coins.data.domain.MarketStatsData
import com.oukachkosnt.coins.data.mappers.toDomainData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MarketStatsApiService {

    fun getGlobalMarketStats(): Single<MarketStatsData> {
        return ApiProvider.marketStatsApi
            .getGlobalStats()
            .subscribeOn(Schedulers.io())
            .map { it.toDomainData() }
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getTopCoinsCapShare(): Single<List<CapShareData>> {
        return ApiProvider.marketStatsApi
            .getTopCoinsCapShare()
            .subscribeOn(Schedulers.io())
            .map { it.map { it.toDomainData() } }
            .observeOn(AndroidSchedulers.mainThread())
    }
}