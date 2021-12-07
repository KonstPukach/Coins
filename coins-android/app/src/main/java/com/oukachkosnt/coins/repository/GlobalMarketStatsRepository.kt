package com.oukachkosnt.coins.repository

import android.annotation.SuppressLint
import com.oukachkosnt.coins.api.servies.MarketStatsApiService
import com.oukachkosnt.coins.data.domain.CapShareData
import com.oukachkosnt.coins.data.domain.MarketStatsData
import com.oukachkosnt.coins.utils.subscribeOnAsError
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

object GlobalMarketStatsRepository {
    private val api = MarketStatsApiService()

    private var activeGlobalStatsRequest: Disposable? = null
    private var activeCapDiagramRequest: Disposable?  = null

    private val cachedGlobalStats    = BehaviorSubject.create<MarketStatsData>()
    private val cachedCapDiagramData = BehaviorSubject.create<List<CapShareData>>()

    private val isGlobalStatsError      = BehaviorSubject.createDefault(false)
    private val isCapDiagramError       = BehaviorSubject.createDefault(false)
    private val isReset                 = BehaviorSubject.createDefault(false)

    fun subscribeOnGlobalStats(
        consumer: (MarketStatsData) -> Unit,
        onError: () -> Unit
    ): Disposable {
        if (!cachedGlobalStats.hasValue()) loadGlobalStats()
        return subscribe(cachedGlobalStats, isGlobalStatsError, consumer, onError)
    }

    fun subscribeOnMarketCapDiagramData(
        consumer: (List<CapShareData>) -> Unit,
        onError: () -> Unit
    ): Disposable {
        if (!cachedCapDiagramData.hasValue()) loadMarketCapDiagramData()
        return subscribe(cachedCapDiagramData, isCapDiagramError, consumer, onError)
    }

    private fun <T> subscribe(
        source: Observable<T>,
        isError: BehaviorSubject<Boolean>,
        consumer: (T) -> Unit,
        onError: () -> Unit
    ): Disposable {
        return CompositeDisposable().also {
            it.addAll(source.subscribe(consumer), isError.subscribeOnAsError(onError))
        }
    }

    fun subscribeOnResetState(consumer: (Boolean) -> Unit): Disposable = isReset.subscribe(consumer)

    @SuppressLint("CheckResult")
    fun reset() {
        if (!isReset.value!!) {
            isReset.onNext(true)
            activeGlobalStatsRequest?.dispose()
            activeCapDiagramRequest?.dispose()
            activeGlobalStatsRequest = null
            activeCapDiagramRequest = null

            val globalStats = api.getGlobalMarketStats()
                .also {
                    it.subscribe(
                        { cachedGlobalStats.onNext(it) },
                        { isGlobalStatsError.onNext(true) }
                    )
                }

            val diagramData = api.getTopCoinsCapShare()
                .also {
                    it.subscribe(
                        { cachedCapDiagramData.onNext(it) },
                        { isCapDiagramError.onNext(true) }
                    )
                }

            Single.zip<Any, Any, Any>(
                globalStats,
                diagramData,
                { _, _ -> Any() }
            ).subscribe({ isReset.onNext(false) }, { isReset.onNext(false) })
        }
    }

    private fun loadGlobalStats() {
        if (activeGlobalStatsRequest == null && !isReset.value!!) {
            activeGlobalStatsRequest =
                api.getGlobalMarketStats()
                    .subscribe(
                        {
                            cachedGlobalStats.onNext(it)
                            activeGlobalStatsRequest = null
                        },
                        {
                            isGlobalStatsError.onNext(true)
                            activeGlobalStatsRequest = null
                        }
                    )
        }
    }

    private fun loadMarketCapDiagramData() {
        if (activeCapDiagramRequest == null && !isReset.value!!) {
            activeCapDiagramRequest =
                api.getTopCoinsCapShare()
                    .subscribe(
                        {
                            cachedCapDiagramData.onNext(it)
                            activeCapDiagramRequest = null
                        },
                        {
                            isCapDiagramError.onNext(true)
                            activeCapDiagramRequest = null
                        }
                    )
        }
    }
}