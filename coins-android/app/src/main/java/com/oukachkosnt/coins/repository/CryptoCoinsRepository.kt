package com.oukachkosnt.coins.repository

import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.data.domain.TopCoins
import com.oukachkosnt.coins.utils.subscribeOnAsError
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import java.util.prefs.Preferences

object CryptoCoinsRepository {
    private val isError       = BehaviorSubject.createDefault(false)
    private val isRefresh     = BehaviorSubject.createDefault(false)
    private val allCoins      = BehaviorSubject.create<List<CryptoCoinData>>()
    private val favoriteCoins = BehaviorSubject.create<List<CryptoCoinData>>()
    private val top10Coins    = BehaviorSubject.create<TopCoins>()

    private val stub = CryptoCoinData(
        id = "",
        name = "Name",
        iconUrl = "https://nemcd.com/wp-content/uploads/2011/07/bitcoin.png",
        symbol = "$",
        rank = 1,
        isFavorite = true,
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

    private val stubTop = TopCoins(
        popular = listOf(stub, stub, stub, stub),
        gainers = listOf(stub, stub, stub, stub),
        losers  = listOf(stub, stub, stub, stub)
    )

    fun stub() {
        allCoins.onNext(listOf(
            stub, stub.copy(isFavorite = false), stub, stub.copy(isFavorite = false), stub
        ))
    }

    fun stubFavorite() {
        favoriteCoins.onNext(listOf(
            stub, stub
        ))
    }

    fun stubTop10Coins() {
        top10Coins.onNext(stubTop)
    }

    fun getAllCoins(): Observable<List<CryptoCoinData>> {
        return allCoins
    }

    fun getFavoriteCoins(): Observable<List<CryptoCoinData>> {
        return allCoins.map { it.filter { data -> data.isFavorite } }
    }

    fun getTop10Coins(): Observable<TopCoins> {
        return top10Coins
    }

    fun getCoinById(id: String): Observable<CryptoCoinData> = allCoins.map { it.find { it.id == id }!! }

    fun switchFavoriteStatus(
        coin: CryptoCoinData,
        prefs: Preferences? = null, // TODO
        onFirstTimeFavorite: () -> Unit,
        onFirstTimeUnfavorite: () -> Unit
    ) {
       // do smth
    }

    fun subscribeOnCoinsError(onError: () -> Unit): Disposable = isError.subscribeOnAsError(onError)

    fun subscribeOnRefreshState(consumer: (Boolean) -> Unit): Disposable = isRefresh.subscribe(consumer)
}