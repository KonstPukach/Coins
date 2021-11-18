package com.oukachkosnt.coins.ui.coins.details

import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.mvp.MvpPresenter
import com.oukachkosnt.coins.repository.CryptoCoinsRepository
import com.oukachkosnt.coins.ui.coins.details.CoinDetailsPagesView
import java.util.prefs.Preferences

class CoinDetailsPagesPresenter(
    view: CoinDetailsPagesView,
    private val coinId: String,
    private val prefs: Preferences? = null
) : MvpPresenter<CoinDetailsPagesView>(view) {

    var coin: CryptoCoinData? = null
        private set

    override fun init() {
        addSubscription(
            CryptoCoinsRepository
                .getCoinById(coinId)
                .subscribe(
                    {
                        coin = it
                        view?.setData(it)
                    },
                    { /*Handled in fragments inside view pager*/ }
                )
        )
    }

    fun switchFavoriteStatus() {
        coin?.also {
            CryptoCoinsRepository.switchFavoriteStatus(
                it,
                prefs,
                { view?.showFirstFavoriteHelpMessage() },
                { view?.showFirstUnfavoriteHelpMessage() }
            )
        } ?: view?.showError()
    }
}