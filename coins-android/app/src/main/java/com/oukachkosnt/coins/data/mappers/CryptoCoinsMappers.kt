package com.oukachkosnt.coins.data.mappers

import com.oukachkosnt.coins.data.api.CryptoCoinApiData
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import java.util.*
import java.util.concurrent.TimeUnit

fun CryptoCoinApiData.toDomainData(coinIdV2: Int?) = toDomainData(
    coinIdV2?.takeIf { it > 0 }
        ?.let { "https://s2.coinmarketcap.com/static/img/coins/128x128/$it.png" }
)

fun CryptoCoinApiData.toDomainData(iconUrl: String? = icon_url) = CryptoCoinData(
    id,
    name,
    iconUrl,
    symbol,
    rank,
    false,
    price_usd,
    price_btc,
    `24h_volume_usd`,
    market_cap_usd,
    available_supply,
    total_supply,
    percent_change_1h,
    percent_change_24h,
    percent_change_7d,
    Date(TimeUnit.SECONDS.toMillis(last_updated))
)
