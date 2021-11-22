package com.oukachkosnt.coins.data.mappers

import com.oukachkosnt.coins.data.api.ExchangeRatesApiData
import com.oukachkosnt.coins.data.api.UsdBasedExchangeRateApiData
import com.oukachkosnt.coins.data.domain.*

fun ExchangeRatesApiData.toDomainData() =
    ExchangeRatesDataSet(currencies.toDomainData().filterByrAndBtc(), cryptocoins.toDomainData())

private fun List<UsdBasedExchangeRateApiData>.toDomainData() =
    map { ExchangeRate(Currency.USD, Currency(it.currency_id, it.name), normalizeRate(it.rate)) }

// FIXME: remove BYR and BTC on server side
private fun List<ExchangeRate>.filterByrAndBtc(): List<ExchangeRate> {
    val currenciesToExclude = setOf("BYR", "BTC")

    return filter { it.targetCurrency.name !in currenciesToExclude }
}

// FIXME: hack for zero rates from server
private fun normalizeRate(rate: Double) = if (rate == 0.0) 1e-9 else rate