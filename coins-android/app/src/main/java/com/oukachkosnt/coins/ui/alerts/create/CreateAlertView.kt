package com.oukachkosnt.coins.ui.alerts.create

import androidx.annotation.StringRes
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.mvp.MvpView

interface CreateAlertView : MvpView {
    fun showErrorState()

    fun showCreateAlertError(@StringRes messageId: Int)
    fun showLowLimitErrorHint(@StringRes messageId: Int?)
    fun showHighLimitErrorHint(@StringRes messageId: Int?)

    fun setCoin(coin: CryptoCoinData)
    fun setLowLimit(price: Double)
    fun setHighLimit(price: Double)

    fun navigateBack()
}