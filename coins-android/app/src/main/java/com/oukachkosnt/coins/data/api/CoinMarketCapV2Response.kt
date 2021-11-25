package com.oukachkosnt.coins.data.api

class CoinMarketCapV2Response<out T>(val data: List<T>)

class CoinMarketCapListingItem(val id: Int, val website_slug: String)
