package com.oukachkosnt.coins.data.api

class CryptoCoinApiData(
    val id: String,
    val name: String,
    val icon_url: String?,
    val symbol: String,
    val rank: Int,
    val price_usd: Double,
    val price_btc: Double,
    val `24h_volume_usd`: Double,
    val market_cap_usd: Double,
    val available_supply: Double,
    val total_supply: Double,
    val percent_change_1h: Double,
    val percent_change_24h: Double,
    val percent_change_7d: Double,
    val last_updated: Long
)
