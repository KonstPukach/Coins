package com.oukachkosnt.coins.formatters

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private val dateTimeFormatter = SimpleDateFormat("dd.MM.yyyy 'at' HH:mm", Locale.US)
private val timeFormatter = SimpleDateFormat("'at' HH:mm:ss", Locale.US)

private val usdPriceFormatter = NumberFormat.getCurrencyInstance(Locale.US)
                                            .also {
                                                it.currency = Currency.getInstance("USD")
                                                it.minimumFractionDigits = 2
                                            }

fun Double.formatPriceUsd(maxFractionDigits: Int? = null): String {
    usdPriceFormatter.maximumFractionDigits = maxFractionDigits ?: 12
    return usdPriceFormatter.format(this)
}

fun Date.formatDateTime(): String = dateTimeFormatter.format(Date(this.time + TIME_OFFSET))
fun Date.formatTime(): String = timeFormatter.format(this)

fun Double.formatPercent(): String = "%.2f %%".format(this)

fun Double.formatPrice(maxFractionDigits: Int? = null) = formatPriceUsd(maxFractionDigits).substring(1)

fun Double.formatPriceUsdAutoprecision() = formatPriceUsd(if (this > 1) 2 else 12)

fun Double.formatPriceAutoprecision() = formatPriceUsd(if (this > 1) 2 else 12).substring(1)

fun Date.formatLabel(scale: Long): String {
    val formatter = when {
        scale < TimeUnit.DAYS.toMillis(1)  -> SimpleDateFormat("HH:mm", Locale.US)
        scale < TimeUnit.DAYS.toMillis(30) -> SimpleDateFormat("dd.MM", Locale.US)
        else                                       -> SimpleDateFormat("MM/yy", Locale.US)
    }

    return formatter.format(this)
}

private val decimalNumberFormatter =
    DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US)).also {
        it.roundingMode = RoundingMode.CEILING
    }

fun Double.formatDecimal(): String {
    decimalNumberFormatter.maximumFractionDigits = 12
    return decimalNumberFormatter.format(this)
}

fun Double.formatEditAlertPrice(): String {
    decimalNumberFormatter.maximumFractionDigits = if (this > 1) 2 else 12
    return decimalNumberFormatter.format(this)
}

private const val TIME_OFFSET = 60 * 3 * 60 * 1000L