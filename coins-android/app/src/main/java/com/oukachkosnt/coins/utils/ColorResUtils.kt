package com.oukachkosnt.coins.utils

import android.content.Context
import com.oukachkosnt.coins.R

fun Context.getColorForPriceChange(change: Double): Int {
    return resources.getColor(
        when {
            change > 0 -> R.color.price_change_grow
            change < 0 -> R.color.price_change_fall
            else       -> R.color.text_color_black
        },
        null
    )
}