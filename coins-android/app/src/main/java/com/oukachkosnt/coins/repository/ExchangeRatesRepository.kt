package com.oukachkosnt.coins.repository

import com.oukachkosnt.coins.api.servies.ExchangeRateApiService
import com.oukachkosnt.coins.data.domain.ExchangeRate
import com.oukachkosnt.coins.data.domain.ExchangeRatesDataSet
import com.oukachkosnt.coins.utils.subscribeOnAsError
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

object ExchangeRatesRepository {
    private val api = ExchangeRateApiService()

    private val cachedExchangeRates = BehaviorSubject.create<ExchangeRatesDataSet>()
    private val isExchangeRatesError = BehaviorSubject.createDefault(false)
    private val isExchangeRatesRefresh = BehaviorSubject.createDefault(false)
    private var activeExchangeRatesRequest: Disposable? = null

    fun subscribeOnExchangeRates(
        consumer: (ExchangeRatesDataSet) -> Unit,
        onError: () -> Unit
    ): Disposable {
        if (!cachedExchangeRates.hasValue()) {
            loadExchangeRates()
        }

        return CompositeDisposable().also {
            it.addAll(
                cachedExchangeRates.subscribe(consumer),
                isExchangeRatesError.subscribeOnAsError(onError)
            )
        }
    }

    fun subscribeOnExchangeRatesRefresh(consumer: (Boolean) -> Unit): Disposable =
        isExchangeRatesRefresh.subscribe(consumer)

    fun getCurrencyExchangeRatesObservable(): Observable<List<ExchangeRate>> {
        if (!cachedExchangeRates.hasValue()) {
            loadExchangeRates()
        }

        return BehaviorSubject.create<List<ExchangeRate>>()
            .also { subject ->
                cachedExchangeRates.subscribe { subject.onNext(it.currencies) }

                // TODO: improve error handling
                isExchangeRatesError.subscribe {
                    if (it) {
                        subject.onNext(listOf(ExchangeRate.USD_EXCHANGE_RATE))
                    }
                }
            }
    }

    fun getCachedCurrencyExchangeRates(): List<ExchangeRate>? =
        cachedExchangeRates.value?.currencies

    fun refreshExchangeRates() {
        if (!isExchangeRatesRefresh.value!!) {
            activeExchangeRatesRequest?.dispose()
            activeExchangeRatesRequest = null
            isExchangeRatesRefresh.onNext(true)
            loadExchangeRates()
        }
    }

    private fun loadExchangeRates() {
        fun onCompleted(isError: Boolean) {
            isExchangeRatesRefresh.onNext(false)
            isExchangeRatesError.onNext(isError)
            activeExchangeRatesRequest = null
        }

        if (activeExchangeRatesRequest == null) {
            activeExchangeRatesRequest =
                api.getUsdBasedExchangeRates()
                    .subscribe(
                        {
                            onCompleted(false)
                            cachedExchangeRates.onNext(it)
                        },
                        {
                            onCompleted(true)
                        }
                    )
        }
    }
}
