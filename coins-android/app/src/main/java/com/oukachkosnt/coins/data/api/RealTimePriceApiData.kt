package com.oukachkosnt.coins.data.api

class RealTimePriceApiData(val msg: RealTimePriceApiMessage)

class RealTimePriceApiMessage(val price: Double, val short: String)