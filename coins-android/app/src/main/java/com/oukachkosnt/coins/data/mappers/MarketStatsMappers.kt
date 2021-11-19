package com.oukachkosnt.coins.data.mappers

import com.oukachkosnt.coins.data.api.CapShareApiData
import com.oukachkosnt.coins.data.api.MarketStatsApiData
import com.oukachkosnt.coins.data.domain.CapShareData
import com.oukachkosnt.coins.data.domain.MarketStatsData
import java.util.*
import java.util.concurrent.TimeUnit

fun MarketStatsApiData.toDomainData()
        = MarketStatsData(total_market_cap_usd,
                          total_24h_volume_usd,
                          bitcoin_percentage_of_market_cap,
                          active_currencies,
                          active_assets,
                          active_markets,
                          Date(TimeUnit.SECONDS.toMillis(last_updated)))

fun CapShareApiData.toDomainData() = CapShareData(name, share)
