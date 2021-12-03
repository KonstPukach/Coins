package com.oukachkosnt.coins

import android.app.Application
import com.oukachkosnt.coins.repository.CryptoCoinsRepository

class CoinsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        CryptoCoinsRepository.init(applicationContext)
    }
}