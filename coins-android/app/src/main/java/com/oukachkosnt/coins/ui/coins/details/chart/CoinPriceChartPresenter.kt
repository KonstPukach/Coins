package com.oukachkosnt.coins.ui.coins.details.chart

import com.oukachkosnt.coins.Preferences
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.data.domain.Currency
import com.oukachkosnt.coins.data.domain.TimeInterval
import com.oukachkosnt.coins.mvp.MvpPresenter
import com.oukachkosnt.coins.repository.CoinChartsRepository
import com.oukachkosnt.coins.repository.ExchangeRatesRepository
import io.reactivex.disposables.Disposable
import java.io.Serializable

class CoinPriceChartPresenter(
    private val coin: CryptoCoinData,
    view: CoinPriceChartView,
    private val prefs: Preferences
) : MvpPresenter<CoinPriceChartView>(view) {

    private var coinPriceHistorySubscription: Disposable? = null
    private var currentCurrency = Currency.USD

    override fun restoreState(state: Serializable?) {
        currentCurrency = state as? Currency ?: Currency.USD
    }

    override fun saveState() = currentCurrency

    override fun init() {
        view?.setChartPage(prefs.coinPriceChartTimeInterval)
        view?.setCurrency(currentCurrency)

        addSubscription(
            CoinChartsRepository.subscribeOnResetState { view?.setRefresh(it) }
        )

        subscribeOnPriceHistory()
    }

    fun refresh() {
        CoinChartsRepository.reset()
    }

    fun setChartInterval(interval: TimeInterval) {
        if (interval == prefs.coinPriceChartTimeInterval) return

        prefs.coinPriceChartTimeInterval = interval
        view?.setChartPage(interval)

        subscribeOnPriceHistory()
    }

    fun setExchangeCurrency(currency: Currency) {
        if (currency == currentCurrency) return

        currentCurrency = currency
        view?.setCurrency(currency)

        subscribeOnPriceHistory()
    }

    fun getCurrencies(): List<Currency>? = ExchangeRatesRepository.getCachedCurrencyExchangeRates()
        ?.map { it.targetCurrency }

    private fun subscribeOnPriceHistory() {
        coinPriceHistorySubscription?.dispose()
        coinPriceHistorySubscription =
            CoinChartsRepository
                .subscribeOnCoinPriceHistory(
                    coin.id,
                    prefs.coinPriceChartTimeInterval,
                    currentCurrency,
                    { view?.setData(it) },
                    { view?.showError() }
                )
    }

    override fun onViewDestroyed() {
        coinPriceHistorySubscription?.dispose()
        super.onViewDestroyed()
    }
}