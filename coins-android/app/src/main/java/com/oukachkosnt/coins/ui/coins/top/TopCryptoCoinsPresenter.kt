package com.oukachkosnt.coins.ui.coins.top

import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.mvp.list.ListMvpPresenter
import com.oukachkosnt.coins.repository.CryptoCoinsRepository
import java.io.Serializable
import java.util.prefs.Preferences

class TopCryptoCoinsPresenter(view: TopCryptoCoinsView,
                              private val prefs: Preferences? = null
) : ListMvpPresenter<TopCryptoCoinsView>(view) {

    override fun init() {
        addSubscription(CryptoCoinsRepository.getInstance().getTop10Coins().subscribe { view?.setData(it) })
        addSubscription(CryptoCoinsRepository.getInstance().subscribeOnCoinsError { view?.showError() })
        addSubscription(CryptoCoinsRepository.getInstance().subscribeOnRefreshState { view?.setRefreshState(it) })
    }

    override fun refreshData() {
       CryptoCoinsRepository.getInstance().refreshAllCoins()
    }

    fun switchCoinFavorite(coin: CryptoCoinData) {
        CryptoCoinsRepository.getInstance().switchFavoriteStatus(
            coin,
            prefs,
            { view?.showFirstFavoriteHelpMessage() },
            { view?.showFirstUnfavoriteHelpMessage() }
        )
    }
}