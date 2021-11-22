package com.oukachkosnt.coins.data.api

class ExchangeRatesApiData(
    val cryptocoins: List<UsdBasedExchangeRateApiData>,
    val currencies: List<UsdBasedExchangeRateApiData>
)

class UsdBasedExchangeRateApiData(
    val currency_id: String,
    val name: String,
    val rate: Double
)
