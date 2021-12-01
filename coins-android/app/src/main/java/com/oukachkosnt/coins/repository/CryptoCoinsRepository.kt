package com.oukachkosnt.coins.repository

import android.annotation.SuppressLint
import com.oukachkosnt.coins.api.db.CryptoCoinEntity
import com.oukachkosnt.coins.api.db.FavsDatabase
import com.oukachkosnt.coins.api.db.mapToEntity
import com.oukachkosnt.coins.api.servies.CryptoCoinsApiService
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.data.domain.TopCoins
import com.oukachkosnt.coins.utils.subscribeOnAsError
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.util.prefs.Preferences

object CryptoCoinsRepository {
    private val coinsApi = CryptoCoinsApiService()

    private val allCoins      = BehaviorSubject.create<List<CryptoCoinData>>()
    private val isError       = BehaviorSubject.createDefault(false)
    private val isRefresh     = BehaviorSubject.createDefault(false)
    private val coinsFromApi = BehaviorSubject.create<List<CryptoCoinData>>()
    private val favoriteCoins = BehaviorSubject.create<List<CryptoCoinData>>()
    private val top10Coins    = BehaviorSubject.create<TopCoins>()
    private var activeCoinsRequest: Disposable? = null

    init {

        coinsFromApi.toFlowable(BackpressureStrategy.LATEST)
        .observeOn(Schedulers.computation())
            .also { it ->
                fun <T> subscribe(subject: Subject<T>, mapper: (List<CryptoCoinData>) -> T) {
                    it.map(mapper)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ subject.onNext(it) }, { isError.onNext(true) })
                }

                subscribe(allCoins, { it })
                subscribe(top10Coins, this::selectTop10)
            }

//        val favsFlowable = favsDb.favsDao().getFavs().toFlowable()
//
//        Flowable.combineLatest(
//            coinsFromApi.toFlowable(BackpressureStrategy.LATEST),
//            favsFlowable.map { it.map { it.id } },
//            { x: List<CryptoCoinData>, y: List<String> -> y to x }
//        ).observeOn(Schedulers.computation())
//            .map { (favorites, coins) ->
//                val favoritesSet = favorites.toSet()
//                coins.map {
//                    if (it.id in favoritesSet) it.copy(isFavorite = true)
//                    else                       it
//                }
//            }
//            .also { it ->
//                fun <T> subscribe(subject: Subject<T>, mapper: (List<CryptoCoinData>) -> T) {
//                    it.map(mapper)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(
//                            { subject.onNext(it) },
//                            { isError.onNext(true) }
//                        )
//                }
//                subscribe(allCoins) { it }
//                subscribe(top10Coins, this::selectTop10)
//            }
    }

    fun subscribeOnAllCoins(consumer: (List<CryptoCoinData>) -> Unit, onError: () -> Unit): Disposable {
        if (!coinsFromApi.hasValue()) {
            loadAllCoins()
        }

        return CompositeDisposable().also {
            it.addAll(allCoins.subscribe(consumer), isError.subscribeOnAsError(onError))
        }
    }

    fun getAllCoins(): Observable<List<CryptoCoinData>> {
        if (!coinsFromApi.hasValue()) {
            loadAllCoins()
        }

        return allCoins
    }

    fun getFavoriteCoins(): Observable<List<CryptoCoinData>> {
        return allCoins.map { it.filter { data -> data.isFavorite } }
    }

    fun getTop10Coins(): Observable<TopCoins> {
        return top10Coins
    }

    fun refreshAllCoins() {
        isRefresh.onNext(true)
        loadAllCoins()
    }

    fun getCoinById(id: String): Observable<CryptoCoinData> = allCoins.map { it.find { it.id == id }!! }

    @SuppressLint("CheckResult")
    fun switchFavoriteStatus(
        coin: CryptoCoinData,
        prefs: Preferences? = null, // TODO
        onFirstTimeFavorite: () -> Unit,
        onFirstTimeUnfavorite: () -> Unit
    ) {
//        if (coin.isFavorite) {
//            favsDb.favsDao().deleteFav(coin.mapToEntity()).doOnComplete(onFirstTimeUnfavorite)
//        } else {
//            favsDb.favsDao().insertFav(coin.mapToEntity()).doOnComplete(onFirstTimeFavorite)
//        }

    }

    private fun loadAllCoins() {
        fun onComplete(error: Boolean) {
            activeCoinsRequest = null
            isError.onNext(error)
            isRefresh.onNext(false)
        }

        if (activeCoinsRequest == null) {
            activeCoinsRequest = coinsApi.getAllCoins()
                .subscribe(
                    { it ->
                        coinsFromApi.onNext(it)
                        onComplete(false)
                    },
                    {
                        onComplete(true)
                    }
                )
        }
    }

    private fun selectTop10(coins: List<CryptoCoinData>): TopCoins {
        val topPopular = coins.sortedByDescending { it.marketCapitalizationUsd }.take(10)

        coins.sortedBy { it.percentChange24h }
            .let {
                return TopCoins(topPopular, it.takeLast(10).asReversed(), it.take(10))
            }
    }

    fun subscribeOnCoinsError(onError: () -> Unit): Disposable = isError.subscribeOnAsError(onError)

    fun subscribeOnRefreshState(consumer: (Boolean) -> Unit): Disposable = isRefresh.subscribe(consumer)
}