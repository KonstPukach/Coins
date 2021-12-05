package com.oukachkosnt.coins.data.mappers

import com.oukachkosnt.coins.data.api.AlertApiData
import com.oukachkosnt.coins.data.domain.AlertData

fun AlertData.toApiObject() = AlertApiData(id, firebaseToken, coinId, lowLimit, highLimit, createdAt, name)


fun AlertApiData.toDomainObject()
    = AlertData(
        id = alert_id,
        firebaseToken = "",
        coinId = coin_id,
        name = name,
        lowLimit = low_limit,
        highLimit = high_limit,
        createdAt = created_at
    )