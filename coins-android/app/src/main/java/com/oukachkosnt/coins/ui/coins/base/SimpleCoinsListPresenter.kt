package com.oukachkosnt.coins.ui.coins.base

import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.mvp.list.ListMvpPresenter
import com.oukachkosnt.coins.repository.CryptoCoinsRepository

abstract class SimpleCoinsListPresenter<V: SimpleCoinsListView>(view: V)
    : ListMvpPresenter<V>(view) {

    override fun refreshData() {
       // CryptoCoinsRepository.refreshAllCoins()
    }

    fun switchCoinFavorite(coin: CryptoCoinData) {
        CryptoCoinsRepository.switchFavoriteStatus(
            coin = coin,
            prefs = null,
            onFirstTimeFavorite = { view?.showFirstFavoriteHelpMessage() },
            onFirstTimeUnfavorite = { view?.showFirstUnfavoriteHelpMessage() }
        )
    }
}