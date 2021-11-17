package com.oukachkosnt.coins.formatters

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private val dateTimeFormatter = SimpleDateFormat("dd.MM.yyyy 'at' HH:mm", Locale.US)

private val usdPriceFormatter = NumberFormat.getCurrencyInstance(Locale.US)
                                            .also {
                                                it.currency = Currency.getInstance("USD")
                                                it.minimumFractionDigits = 2
                                            }

fun Double.formatPriceUsd(maxFractionDigits: Int? = null): String {
    usdPriceFormatter.maximumFractionDigits = maxFractionDigits ?: 12
    return usdPriceFormatter.format(this)
}

fun Date.formatDateTime(): String = dateTimeFormatter.format(this)

fun Double.formatPercent(): String = "%.2f %%".format(this)
