package com.oukachkosnt.coins.ui.coins.all

import com.oukachkosnt.coins.ui.coins.base.SimpleCoinsListFragment

class AllCryptoCoinsListFragment : SimpleCoinsListFragment<AllCryptoCoinsPresenter>(), AllCryptoCoinsView {

    override fun createPresenter() = AllCryptoCoinsPresenter(this, requireContext().applicationContext)
}