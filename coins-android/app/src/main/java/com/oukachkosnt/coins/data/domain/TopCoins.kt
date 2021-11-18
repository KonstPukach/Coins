package com.oukachkosnt.coins.data.domain

class TopCoins(
    val popular: List<CryptoCoinData>,
    val gainers: List<CryptoCoinData>,
    val losers: List<CryptoCoinData>
)