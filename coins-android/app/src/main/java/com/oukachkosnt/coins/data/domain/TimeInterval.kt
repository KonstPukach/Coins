package com.oukachkosnt.coins.data.domain

import java.util.concurrent.TimeUnit

enum class TimeInterval(@Suppress("unused") val delta: Long) {
    DAY(TimeUnit.DAYS.toMillis(1)),
    WEEK(TimeUnit.DAYS.toMillis(7)),
    MONTH(TimeUnit.DAYS.toMillis(31)),
    YEAR(TimeUnit.DAYS.toMillis(366)),
    ALL_TIME(TimeUnit.DAYS.toMillis(366L * 10))
}
