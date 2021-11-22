package com.oukachkosnt.coins.ui.converter

import com.oukachkosnt.coins.data.domain.ExchangeRate
import com.oukachkosnt.coins.mvp.MvpView

interface ConverterView : MvpView {
    fun showContent()
    fun showError()
    fun setRefreshing(isRefresh: Boolean)

    fun setExchangeRate(exchangeRate: ExchangeRate)

    fun setBaseCurrency(value: Double)
    fun setTargetCurrency(value: Double)

    fun setBaseCurrencyError(isError: Boolean)
    fun setTargetCurrencyError(isError: Boolean)
}