package com.oukachkosnt.coins.data.domain

import java.util.*
import java.io.Serializable

data class CryptoCoinData(
    val id: String,
    val name: String,
    val iconUrl: String?,
    val symbol: String,
    val rank: Int,
    val isFavorite: Boolean,
    val priceUsd: Double,
    val priceBtc: Double,
    val volumeUsd24h: Double,
    val marketCapitalizationUsd: Double,
    val availableSupply: Double,
    val totalSupply: Double,
    val percentChange1h: Double,
    val percentChange24h: Double,
    val percentChange7d: Double,
    val lastUpdated: Date
) : Serializable