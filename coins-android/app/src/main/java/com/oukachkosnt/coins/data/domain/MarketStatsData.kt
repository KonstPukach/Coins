package com.oukachkosnt.coins.data.domain

import java.util.*

data class MarketStatsData(
    val totalMarketCapUsd: Double,
    val total24hVolumeUsd: Double,
    val bitcoinPartOfCap: Double,
    val activeCurrencies: Int,
    val activeAssets: Int,
    val activeMarkets: Int,
    val timestamp: Date
)

data class CapShareData(
    val name: String,
    val share: Double
)
