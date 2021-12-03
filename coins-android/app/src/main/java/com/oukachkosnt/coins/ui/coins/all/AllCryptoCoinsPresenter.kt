package com.oukachkosnt.coins.ui.coins.all

import android.content.Context
import com.oukachkosnt.coins.repository.CryptoCoinsRepository
import com.oukachkosnt.coins.ui.coins.base.SimpleCoinsListPresenter

class AllCryptoCoinsPresenter(
    view: AllCryptoCoinsView,
    private val applicationContext: Context
) : SimpleCoinsListPresenter<AllCryptoCoinsView>(
    view
) {

    override fun init() {
        with(CryptoCoinsRepository.init(applicationContext)) {
            addSubscription(getAllCoins().subscribe { view?.setData(it) })
            addSubscription(subscribeOnCoinsError { view?.showError() })
            addSubscription(subscribeOnRefreshState { view?.setRefreshState(it) })
        }
    }
}