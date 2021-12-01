package com.oukachkosnt.coins.data.mappers

import com.oukachkosnt.coins.data.api.CapShareApiData
import com.oukachkosnt.coins.data.api.MarketStatsApiData
import com.oukachkosnt.coins.data.domain.CapShareData
import com.oukachkosnt.coins.data.domain.MarketStatsData
import java.util.*
import java.util.concurrent.TimeUnit

fun MarketStatsApiData.toDomainData() = MarketStatsData(
    totalMarketCapUsd,
    total24hVolumeUsd,
    bitcoinPercentageOfMarketCap,
    activeCurrencies,
    activeAssets,
    activeMarkets,
    Date(TimeUnit.SECONDS.toMillis(lastUpdated))
)

fun CapShareApiData.toDomainData() = CapShareData(name, share)
