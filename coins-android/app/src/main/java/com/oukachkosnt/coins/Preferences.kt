package com.oukachkosnt.coins

import android.content.Context
import com.oukachkosnt.coins.data.domain.TimeInterval
import net.hockeyapp.android.tasks.ParseFeedbackTask.PREFERENCES_NAME
import kotlin.reflect.KProperty

class Preferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    var marketCapShareTimeInterval by PrefProperty(
        key     = MARKET_CAP_SHARE_CHART_INTERVAL,
        mapper  = TimeInterval::valueOf,
        default = TimeInterval.MONTH
    )

    var coinPriceChartTimeInterval by PrefProperty(
        key     = COIN_PRICE_CHART_INTERVAL,
        mapper  = TimeInterval::valueOf,
        default = TimeInterval.MONTH
    )

    var firebaseToken by PrefProperty(FIREBASE_TOKEN_KEY, { it }, null)

    private inner class PrefProperty<T>(
        private val key: String,
        private val mapper: (String) -> T,
        private val default: T
    ) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return sharedPreferences.getString(key, null)
                ?.let { mapper(it) }
                ?: default
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            sharedPreferences.edit()
                .putString(key, value.toString())
                .apply()
        }
    }

    companion object {
        private const val COIN_PRICE_CHART_INTERVAL = "coinPriceIntervalKey"
        private const val MARKET_CAP_SHARE_CHART_INTERVAL = "capShareIntervalKey"
        private const val FIREBASE_TOKEN_KEY = "firebaseTokenKey"
    }
}