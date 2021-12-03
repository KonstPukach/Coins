package com.oukachkosnt.coins.ui.coins.details.coin

import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.mvp.MvpPresenter
import com.oukachkosnt.coins.repository.CryptoCoinsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class CoinDetailsPresenter(
    private val coin: CryptoCoinData,
    realTimePriceServiceProvider: RealTimePriceServiceProvider,
    view: CoinDetailsView
) : MvpPresenter<CoinDetailsView>(view) {

    private val realTimePriceService = realTimePriceServiceProvider.getRealTimePriceService()

    override fun init() {
        addSubscription(
            CryptoCoinsRepository
                    .getCoinById(coin.id)
                    .subscribe(
                        { view?.setData(it) },
                        { view?.showError() }
                    )
        )

        addSubscription(
                realTimePriceService
                    .getPriceDataObservable()
                    .subscribe {
                        view?.setRealTimePrice(it)
                    }
        )

        addSubscription(
                realTimePriceService
                    .getConnectionStateObservable()
                    .subscribe {
                        //view?.setRealTimePriceConnectionState(it)
                    }
        )

        addSubscription(
                realTimePriceService
                    .getErrorObservable()
                    .subscribe {
                        //view?.setRealTimePriceError()
                    }
        )
    }

    fun onViewShown() {
        realTimePriceService.connect()
    }

    fun onViewHidden() {
        realTimePriceService.disconnect()
    }
}