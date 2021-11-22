package com.oukachkosnt.coins.api.servies

import com.oukachkosnt.coins.api.retrofit.ApiProvider
import com.oukachkosnt.coins.data.domain.ExchangeRatesDataSet
import com.oukachkosnt.coins.data.mappers.toDomainData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ExchangeRateApiService {

    fun getUsdBasedExchangeRates(): Single<ExchangeRatesDataSet>
        = ApiProvider.exchangeRateApi
            .getUsdBasedExchangeRates()
            .subscribeOn(Schedulers.io())
            .map { it.toDomainData() }
            .observeOn(AndroidSchedulers.mainThread())
}