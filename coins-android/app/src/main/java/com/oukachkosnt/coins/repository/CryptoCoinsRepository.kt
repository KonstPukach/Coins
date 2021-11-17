package com.oukachkosnt.coins.repository

import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.utils.subscribeOnAsError
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.*

object CryptoCoinsRepository {
    private val isError       = BehaviorSubject.createDefault(false)
    private val isRefresh     = BehaviorSubject.createDefault(false)
    private val allCoins = BehaviorSubject.create<List<CryptoCoinData>>()

    private val stub = CryptoCoinData(
        id = "",
        name = "Name",
        iconUrl = "https://nemcd.com/wp-content/uploads/2011/07/bitcoin.png",
        symbol = "$",
        rank = 1,
        isFavorite = false,
        priceUsd = 123.32,
        priceBtc = 1232.32,
        volumeUsd24h = 123.32,
        marketCapitalizationUsd = 123.232,
        availableSupply = 123.32,
        totalSupply = 1233.23,
        percentChange1h = 13.12,
        percentChange24h = 31.32,
        percentChange7d = 123.21,
        lastUpdated = Date()
    )

    fun stub() {
        allCoins.onNext(listOf(
            stub, stub, stub, stub, stub
        ))
    }

    fun getAllCoins(): Observable<List<CryptoCoinData>> {
        return allCoins
    }

    fun subscribeOnCoinsError(onError: () -> Unit): Disposable
            = isError.subscribeOnAsError(onError)

    fun subscribeOnRefreshState(consumer: (Boolean) -> Unit): Disposable
            = isRefresh.subscribe(consumer)
}