package com.oukachkosnt.coins.api.servies

import com.oukachkosnt.coins.api.retrofit.ApiProvider
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.data.mappers.toDomainData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CryptoCoinsApiService {

    fun getAllCoins(): Single<List<CryptoCoinData>> {
        return ApiProvider.cryptoCoinsApi
            .getAllCryptoCoins()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { coins ->
                coins.map { it.toDomainData() }
            }
            .observeOn(AndroidSchedulers.mainThread())
    }
}