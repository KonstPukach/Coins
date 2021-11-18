package com.oukachkosnt.coins.ui.coins.details.coin

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.oukachkosnt.coins.api.servies.RealTimePriceApiService
import com.oukachkosnt.coins.data.domain.CryptoCoinData

class CoinDetailsStateRetainer : Fragment(), RealTimePriceServiceProvider {
    companion object {
        private const val FRAGMENT_TAG = "coinDetailsRetainer"

        fun getInstance(coin: CryptoCoinData, fragmentManager: FragmentManager): CoinDetailsStateRetainer {
            val retainedInstance = fragmentManager.findFragmentByTag(FRAGMENT_TAG) as? CoinDetailsStateRetainer

            if (retainedInstance == null) {
                val newInstance = CoinDetailsStateRetainer().also {
                    it.priceService = RealTimePriceApiService(coin)
                }

                fragmentManager.beginTransaction()
                        .add(newInstance, FRAGMENT_TAG)
                        .commit()

                return newInstance

            } else if (retainedInstance.priceService == null) {
                retainedInstance.priceService = RealTimePriceApiService(coin)
            }

            return retainedInstance
        }
    }

    private var priceService: RealTimePriceApiService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun getRealTimePriceService() = priceService!!
}

interface RealTimePriceServiceProvider {
    fun getRealTimePriceService(): RealTimePriceApiService
}