package com.oukachkosnt.coins.data.domain

import java.io.Serializable

data class Currency(val id: String, val name: String) : Serializable {
    companion object {
        val USD = Currency("USD", "USD")
    }
}

data class ExchangeRate(
    val baseCurrency: Currency,
    val targetCurrency: Currency,
    val rate: Double
) : Serializable {
    companion object {
        val USD_EXCHANGE_RATE = ExchangeRate(Currency.USD, Currency.USD, 1.0)
    }

    init {
        if (rate <= 0) throw IllegalArgumentException("rate must be positive")
    }

    fun getCrossExchangeRate(asTarget: ExchangeRate): ExchangeRate {
        if (baseCurrency != asTarget.baseCurrency) throw IllegalArgumentException("Must have one base")

        return ExchangeRate(targetCurrency, asTarget.targetCurrency, asTarget.rate / rate)
    }

    fun swap() = ExchangeRate(targetCurrency, baseCurrency, 1 / rate)
}

class ExchangeRatesDataSet(val currencies: List<ExchangeRate>, val cryptoCoins: List<ExchangeRate>)