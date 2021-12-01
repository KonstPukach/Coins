package com.oukachkosnt.coins.repository

import android.annotation.SuppressLint
import com.oukachkosnt.coins.api.servies.ChartsApiService
import com.oukachkosnt.coins.data.domain.Currency
import com.oukachkosnt.coins.data.domain.ExchangeRate
import com.oukachkosnt.coins.data.domain.TimeInterval
import com.oukachkosnt.coins.utils.subscribeOnAsError
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import java.util.*

object CoinChartsRepository {
    private val api = ChartsApiService()

    private val activeCoinPriceHistoryRequests = mutableMapOf<TimeInterval, Disposable>()
    private var lastTimeInterval = TimeInterval.DAY
    private var currentCoinId = ""

    private var cachedCoinPriceHistoryChartData = reinitCachedCoinPriceData()
    private val isCoinPriceChartError = BehaviorSubject.createDefault(false)
    private val isReset = BehaviorSubject.createDefault(false)

    fun subscribeOnCoinPriceHistory(
        coinId: String,
        interval: TimeInterval,
        exchangeCurrency: Currency,
        consumer: (List<Pair<Date, Double>>) -> Unit,
        onError: () -> Unit
    ): Disposable {
        lastTimeInterval = interval

        if (currentCoinId != coinId) {
            currentCoinId = coinId

            activeCoinPriceHistoryRequests.values.forEach { it.dispose() }
            activeCoinPriceHistoryRequests.clear()

            cachedCoinPriceHistoryChartData = reinitCachedCoinPriceData()
        }

        if (!cachedCoinPriceHistoryChartData.getValue(interval).hasValue()) {
            loadCoinPriceChartData(interval)
        }

        val data = Observable.combineLatest(
            cachedCoinPriceHistoryChartData.getValue(interval),
            ExchangeRatesRepository.getCurrencyExchangeRatesObservable(),
            BiFunction { x: List<Pair<Date, Double>>, y: List<ExchangeRate> -> x to y }
        )
            .map { (points, rates) ->
                val rate = rates.find { it.targetCurrency == exchangeCurrency }!!
                points.map { (date, price) -> date to price * rate.rate }
            }

        return CompositeDisposable().also {
            it.addAll(
                data.subscribe(consumer, { onError() }),
                isCoinPriceChartError.subscribeOnAsError(onError)
            )
        }
    }

    fun subscribeOnResetState(consumer: (Boolean) -> Unit): Disposable = isReset.subscribe(consumer)

    private fun reinitCachedCoinPriceData() =
        TimeInterval.values().associate { it to BehaviorSubject.create<List<Pair<Date, Double>>>() }

    private fun loadCoinPriceChartData(interval: TimeInterval) {
        if (activeCoinPriceHistoryRequests[interval] == null && !isReset.value!!) {
            activeCoinPriceHistoryRequests[interval] =
                api.getCoinPriceChartData(currentCoinId, interval)
                    .subscribe(
                        {
                            cachedCoinPriceHistoryChartData.getValue(interval).onNext(it)
                            activeCoinPriceHistoryRequests.remove(interval)
                        },
                        {
                            isCoinPriceChartError.onNext(true)
                            activeCoinPriceHistoryRequests.remove(interval)
                        }
                    )
        }
    }

    @SuppressLint("CheckResult")
    fun reset() {
        if (!isReset.value!!) {
            isReset.onNext(true)
            activeCoinPriceHistoryRequests[lastTimeInterval]?.dispose()
            activeCoinPriceHistoryRequests.remove(lastTimeInterval)

            ExchangeRatesRepository.refreshExchangeRates()

            val interval = lastTimeInterval
            api.getCoinPriceChartData(currentCoinId, interval)
                .subscribe(
                    {
                        cachedCoinPriceHistoryChartData.getValue(interval).onNext(it)
                        isReset.onNext(false)
                    },
                    {
                        isReset.onNext(false)
                        isCoinPriceChartError.onNext(true)
                    }
                )
        }
    }
}