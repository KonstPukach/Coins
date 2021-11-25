package com.oukachkosnt.coins.api.retrofit

import com.oukachkosnt.coins.data.api.CryptoCoinApiData
import io.reactivex.Single
import retrofit2.http.GET

interface CryptoCoinsApi {
    @GET("coins")
    fun getAllCryptoCoins(): Single<List<CryptoCoinApiData>>
}