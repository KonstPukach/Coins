package com.oukachkosnt.coins.repository

import com.oukachkosnt.coins.api.servies.ChartsApiService
import com.oukachkosnt.coins.api.servies.MarketStatsApiService
import com.oukachkosnt.coins.data.domain.CapShareData
import com.oukachkosnt.coins.data.domain.MarketStatsData
import com.oukachkosnt.coins.data.domain.TimeInterval
import com.oukachkosnt.coins.utils.subscribeOnAsError
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function3
import io.reactivex.subjects.BehaviorSubject
import java.util.*

object GlobalMarketStatsRepository {
    private val api = MarketStatsApiService()
    private val chartsApi = ChartsApiService()

    private var activeGlobalStatsRequest: Disposable? = null
    private var activeCapDiagramRequest: Disposable?  = null

    private val activeCapHistoryRequests = mutableMapOf<TimeInterval, Disposable>()

    private val cachedGlobalStats    = BehaviorSubject.create<MarketStatsData>()
    private val cachedCapDiagramData = BehaviorSubject.create<List<CapShareData>>()

    private val cachedCapHistoryChartData = mapOf(
        TimeInterval.DAY      to BehaviorSubject.create<Map<String, List<Pair<Date, Double>>>>(),
        TimeInterval.MONTH    to BehaviorSubject.create<Map<String, List<Pair<Date, Double>>>>(),
        TimeInterval.YEAR     to BehaviorSubject.create<Map<String, List<Pair<Date, Double>>>>(),
        TimeInterval.ALL_TIME to BehaviorSubject.create<Map<String, List<Pair<Date, Double>>>>()
    )

    private val isGlobalStatsError      = BehaviorSubject.createDefault(false)
    private val isCapDiagramError       = BehaviorSubject.createDefault(false)
    private val isCapHistoryChartError  = BehaviorSubject.createDefault(false)
    private val isReset                 = BehaviorSubject.createDefault(false)

    private var lastTimeInterval = TimeInterval.DAY

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

    fun subscribeOnCapHistory(
        interval: TimeInterval,
        consumer: (Map<String, List<Pair<Date, Double>>>) -> Unit,
        onError: () -> Unit
    ): Disposable = subscribe(
        source   = cachedCapHistoryChartData.getValue(interval),
        isError  = isCapHistoryChartError,
        consumer = consumer,
        onError  = onError
    )

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

    fun loadCapHistoryDataIfMissed(interval: TimeInterval) {
        lastTimeInterval = interval

        if (cachedCapHistoryChartData.getValue(interval).hasValue()) return

        if (activeCapHistoryRequests[interval] == null && !isReset.value!!) {
            activeCapHistoryRequests[interval] =
                chartsApi
                    .getMarketCapShareChartData(interval)
                    .subscribe(
                        {
                            cachedCapHistoryChartData.getValue(interval).onNext(it)
                            activeCapHistoryRequests.remove(interval)
                        },
                        {
                            isCapHistoryChartError.onNext(true)
                            activeCapHistoryRequests.remove(interval)
                        }
                    )
        }
    }

    fun subscribeOnResetState(consumer: (Boolean) -> Unit): Disposable = isReset.subscribe(consumer)

    fun reset() {
        if (!isReset.value!!) {
            isReset.onNext(true)
            activeGlobalStatsRequest?.dispose()
            activeCapDiagramRequest?.dispose()
            activeCapHistoryRequests[lastTimeInterval]?.dispose()
            activeGlobalStatsRequest = null
            activeCapDiagramRequest = null
            activeCapHistoryRequests.remove(lastTimeInterval)

            val globalStats = api.getGlobalMarketStats()
                .also {
                    it.subscribe(
                        { cachedGlobalStats.onNext(it) },
                        { isGlobalStatsError.onNext(true) }
                    )
                }

            val diagramData = api.getTopCoinsCapShare()
                .also {
                    it.subscribe({ cachedCapDiagramData.onNext(it) },
                        { isCapDiagramError.onNext(true) })
                }

            val interval = lastTimeInterval
            val chartData = chartsApi
                .getMarketCapShareChartData(interval)
                .also {
                    it.subscribe({ cachedCapHistoryChartData.getValue(interval).onNext(it) },
                        { isCapHistoryChartError.onNext(true) })
                }

            Single.zip<Any, Any, Any, Any>(
                globalStats,
                diagramData,
                chartData,
                Function3 { _, _, _ -> Any() })
                .subscribe({ isReset.onNext(false) }, { isReset.onNext(false) })
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