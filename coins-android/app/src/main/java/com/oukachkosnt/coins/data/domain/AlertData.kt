package com.oukachkosnt.coins.data.domain

data class AlertData(
    val id: Long?,
    val firebaseToken: String,
    val coinId: String,
    val name: String,
    val lowLimit: Double,
    val highLimit: Double,
    val createdAt: String
)