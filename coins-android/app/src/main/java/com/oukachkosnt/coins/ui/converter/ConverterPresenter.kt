package com.oukachkosnt.coins.ui.converter

import com.oukachkosnt.coins.data.domain.Currency
import com.oukachkosnt.coins.data.domain.ExchangeRate
import com.oukachkosnt.coins.data.domain.ExchangeRatesDataSet
import com.oukachkosnt.coins.mvp.MvpPresenter
import com.oukachkosnt.coins.repository.ExchangeRatesRepository
import java.io.Serializable

class ConverterPresenter(view: ConverterView) : MvpPresenter<ConverterView>(view) {
    private val exchangeRateMap = mutableMapOf<Currency, ExchangeRate>()
    private var rateDataSet: ExchangeRatesDataSet? = null
    private var state = ConverterState()

    override fun saveState() = state

    override fun restoreState(state: Serializable?) {
        this.state = state as? ConverterState ?: ConverterState()
    }

    override fun init() {
        addSubscription(
            ExchangeRatesRepository.subscribeOnExchangeRates(
                {
                    (it.currencies + it.cryptoCoins).forEach { exchangeRateMap[it.targetCurrency] = it }
                    rateDataSet = it

                    if (state.exchangeRate == null) {
                        updateCurrentExchangeRate(
                            it.cryptoCoins.firstOrNull()?.targetCurrency,
                            it.currencies.find { it.targetCurrency == Currency.USD }?.targetCurrency
                        )
                    }

                    showState()
                },
                { view?.showError() }
            )
        )
        addSubscription(
            ExchangeRatesRepository.subscribeOnExchangeRatesRefresh { view?.setRefreshing(it) }
        )
    }

    fun setBaseCurrency(currency: Currency) {
        updateCurrentExchangeRate(currency, state.exchangeRate?.targetCurrency)
        showState()
    }

    fun setTargetCurrency(currency: Currency) {
        updateCurrentExchangeRate(state.exchangeRate?.baseCurrency, currency)
        showState()
    }

    fun swapCurrencies() {
        state = state.swapCurrencies()
        showState()
    }

    fun updateBaseCurrencyValue(asString: String) {
        val newValue = asString.replace(',', '.').toDoubleOrNull()

        if (newValue != state.amountToConvert) {
            state = state.updateAmountToConvert(newValue)
            state.convertedAmount?.also { view?.setTargetCurrency(it) }
        }

        view?.setBaseCurrencyError(newValue == null)
    }

    fun updateTargetCurrencyValue(asString: String) {
        val newValue = asString.replace(',', '.').toDoubleOrNull()

        if (newValue != state.convertedAmount) {
            state = state.updateConvertedAmount(newValue)
            state.amountToConvert?.also { view?.setBaseCurrency(it) }
        }

        view?.setTargetCurrencyError(newValue == null)
    }

    fun refresh() {
        ExchangeRatesRepository.refreshExchangeRates()
    }

    fun getCurrencies(): List<Currency> {
        return rateDataSet?.let {
            it.currencies.map { it.targetCurrency }.sortedBy { it.name } +
                    it.cryptoCoins.map { it.targetCurrency }.sortedBy { it.name }
        }.orEmpty()
    }

    private fun updateCurrentExchangeRate(base: Currency?, target: Currency?) {
        exchangeRateMap[target]?.let { exchangeRateMap[base]?.getCrossExchangeRate(it) }
            ?.also { state = state.updateExchangeRate(it) }
    }

    private fun showState() {
        view?.showContent()

        with(state) {
            exchangeRate?.let { view?.setExchangeRate(it) }
            amountToConvert?.let { view?.setBaseCurrency(it) }
            convertedAmount?.let { view?.setTargetCurrency(it) }
        }
    }

    class ConverterState(
        val exchangeRate: ExchangeRate? = null,
        val amountToConvert: Double? = 0.0,
        val convertedAmount: Double? = 0.0
    ) : Serializable {

        fun swapCurrencies() = ConverterState(
            exchangeRate?.swap(),
            amountToConvert,
            convert(exchangeRate?.swap(), amountToConvert)
        )

        fun updateExchangeRate(rate: ExchangeRate) =
            ConverterState(rate, amountToConvert, convert(rate, amountToConvert))

        fun updateAmountToConvert(value: Double?) =
            ConverterState(exchangeRate, value, convert(exchangeRate, value))

        fun updateConvertedAmount(value: Double?) =
            ConverterState(exchangeRate, convert(exchangeRate?.swap(), value), value)

        private fun convert(exchangeRate: ExchangeRate?, amount: Double?) = exchangeRate?.rate
            ?.let { rate -> amount?.let { it * rate } }
    }
}