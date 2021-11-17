package com.oukachkosnt.coins.ui.coins.base

import com.oukachkosnt.coins.mvp.list.ListMvpView
import com.oukachkosnt.coins.data.domain.CryptoCoinData

interface SimpleCoinsListView : ListMvpView<List<CryptoCoinData>> {
    fun showFirstFavoriteHelpMessage()
    fun showFirstUnfavoriteHelpMessage()
}