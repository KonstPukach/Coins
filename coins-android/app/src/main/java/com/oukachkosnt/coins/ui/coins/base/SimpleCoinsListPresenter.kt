package com.oukachkosnt.coins.ui.coins.base

import com.oukachkosnt.coins.mvp.list.ListMvpPresenter

abstract class SimpleCoinsListPresenter<V: SimpleCoinsListView>(view: V)
    : ListMvpPresenter<V>(view) {

    override fun refreshData() {
       // CryptoCoinsRepository.refreshAllCoins()
    }
}