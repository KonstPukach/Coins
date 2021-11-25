package com.oukachkosnt.coins.api.retrofit

import com.oukachkosnt.coins.data.api.CoinMarketCapListingItem
import com.oukachkosnt.coins.data.api.CoinMarketCapV2Response
import com.oukachkosnt.coins.data.api.CryptoCoinApiData
import io.reactivex.Single
import retrofit2.http.GET

interface CoinMarketCapApi {

    @GET("v1/ticker?limit=0")
    fun getAllCryptoCoins(): Single<List<CryptoCoinApiData>>

    @GET("v2/listings")
    fun getAllCryptoCoinsShortListing(): Single<CoinMarketCapV2Response<CoinMarketCapListingItem>>
}
