package com.oukachkosnt.coins.ui.coins.favorite

import com.oukachkosnt.coins.repository.CryptoCoinsRepository
import com.oukachkosnt.coins.ui.coins.base.SimpleCoinsListPresenter
import java.io.Serializable
import java.util.prefs.Preferences

class FavoriteCryptoCoinsPresenter(
    view: FavoriteCryptoCoinsView,
    prefs: Preferences? = null
) : SimpleCoinsListPresenter<FavoriteCryptoCoinsView>(view) {

    override fun init() {
        addSubscription(CryptoCoinsRepository.getInstance().getFavoriteCoins().subscribe { view?.setData(it) })
        addSubscription(CryptoCoinsRepository.getInstance().subscribeOnCoinsError { view?.showError() })
        addSubscription(CryptoCoinsRepository.getInstance().subscribeOnRefreshState { view?.setRefreshState(it) })
    }
}