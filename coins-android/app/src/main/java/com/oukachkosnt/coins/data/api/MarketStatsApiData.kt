package com.oukachkosnt.coins.data.api

class MarketStatsApiData(
    val total_market_cap_usd: Double,
    val total_24h_volume_usd: Double,
    val bitcoin_percentage_of_market_cap: Double,
    val active_currencies: Int,
    val active_assets: Int,
    val active_markets: Int,
    val last_updated: Long
)

class CapShareApiData(
    val name: String,
    val share: Double
)
