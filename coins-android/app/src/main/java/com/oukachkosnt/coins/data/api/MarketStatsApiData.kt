package com.oukachkosnt.coins.data.api

class MarketStatsApiData(
    val totalMarketCapUsd: Double,
    val total24hVolumeUsd: Double,
    val bitcoinPercentageOfMarketCap: Double,
    val activeCurrencies: Int,
    val activeAssets: Int,
    val activeMarkets: Int,
    val lastUpdated: Long
)

class CapShareApiData(
    val name: String,
    val share: Double
)
