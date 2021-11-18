package com.oukachkosnt.coins.ui.coins.favorite

import com.oukachkosnt.coins.ui.coins.base.SimpleCoinsListFragment

class FavoriteCryptoCoinsFragment : SimpleCoinsListFragment<FavoriteCryptoCoinsPresenter>(),
    FavoriteCryptoCoinsView {

    override fun createPresenter() = FavoriteCryptoCoinsPresenter(this, null)
}