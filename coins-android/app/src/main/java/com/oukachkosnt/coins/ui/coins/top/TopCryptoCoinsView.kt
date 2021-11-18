package com.oukachkosnt.coins.ui.coins.top

import com.oukachkosnt.coins.data.domain.TopCoins
import com.oukachkosnt.coins.mvp.list.ListMvpView

interface TopCryptoCoinsView : ListMvpView<TopCoins> {
    fun showFirstFavoriteHelpMessage()
    fun showFirstUnfavoriteHelpMessage()
}