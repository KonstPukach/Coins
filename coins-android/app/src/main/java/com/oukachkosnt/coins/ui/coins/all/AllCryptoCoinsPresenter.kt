package com.oukachkosnt.coins.ui.coins.all

import com.oukachkosnt.coins.repository.CryptoCoinsRepository
import com.oukachkosnt.coins.ui.coins.base.SimpleCoinsListPresenter
import java.io.Serializable

class AllCryptoCoinsPresenter(view: AllCryptoCoinsView) : SimpleCoinsListPresenter<AllCryptoCoinsView>(view) {

    override fun init() {
        with(CryptoCoinsRepository) {
            addSubscription(getAllCoins().subscribe { view?.setData(it) })
            addSubscription(subscribeOnCoinsError { view?.showError() })
            addSubscription(subscribeOnRefreshState { view?.setRefreshState(it) })
        }
    }

    override fun restoreState(state: Serializable?) {
        super.restoreState(state)
        CryptoCoinsRepository.stub()
    }
}