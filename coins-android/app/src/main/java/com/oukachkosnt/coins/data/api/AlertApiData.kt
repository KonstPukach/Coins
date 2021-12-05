package com.oukachkosnt.coins.data.api

class AlertApiData(
    val alert_id: Long?,
    val token: String,
    val coin_id: String,
    val low_limit: Double,
    val high_limit: Double,
    val created_at: String,
    val name: String
)