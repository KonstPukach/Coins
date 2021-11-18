package com.oukachkosnt.coins.data.mappers

import com.oukachkosnt.coins.data.api.RealTimePriceApiData
import com.oukachkosnt.coins.data.domain.RealTimePriceData

fun RealTimePriceApiData.toDomainObject() = RealTimePriceData(msg.price)