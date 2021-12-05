package com.oukachkosnt.coins.ui.alerts.create

import com.oukachkosnt.coins.Preferences
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.AlertData
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.mvp.MvpPresenter
import com.oukachkosnt.coins.repository.AlertsRepository
import com.oukachkosnt.coins.repository.CryptoCoinsRepository
import net.hockeyapp.android.utils.JSONDateUtils
import java.io.Serializable
import java.util.*

class CreateAlertPresenter(
    view: CreateAlertView,
    private val prefs: Preferences
) : MvpPresenter<CreateAlertView>(view) {
    private var currentCoin: CryptoCoinData? = null
    private var coins: List<CryptoCoinData> = emptyList()

    override fun restoreState(state: Serializable?) {
        currentCoin = state as? CryptoCoinData
    }

    override fun saveState() = currentCoin

    override fun init() {
        addSubscription(
            CryptoCoinsRepository
                .getInstance()
                .subscribeOnAllCoins(
                    consumer = {
                        if (it.isNotEmpty()) {
                            if (currentCoin == null) {
                                with(it.first()) {
                                    view?.setLowLimit(0.9 * priceUsd)
                                    view?.setHighLimit(1.1 * priceUsd)
                                    currentCoin = this
                                }
                            } else {
                                currentCoin = it.findLast { it.id == currentCoin?.id }
                            }

                            currentCoin?.let { view?.setCoin(it) }
                        }

                        coins = it
                    },
                    onError = {
                        if (currentCoin == null) {
                            view?.showErrorState()
                        }
                    }
                )
        )
    }

    fun getCoins() = coins

    fun setCurrentCoin(coin: CryptoCoinData) {
        if (currentCoin != coin) {
            currentCoin = coin

            currentCoin?.let { view?.setCoin(it) }
            view?.setLowLimit(0.9 * coin.priceUsd)
            view?.setHighLimit(1.1 * coin.priceUsd)
        }
    }

    fun createAlert(lowLimit: String, highLimit: String) {
        validateLowLimit(lowLimit)
        validateHighLimit(highLimit)

        currentCoin?.also { coin ->
            val low = lowLimit.replace(',', '.').toDoubleOrNull()
            val high = highLimit.replace(',', '.').toDoubleOrNull()

            if (low != null && high != null) {
                if (low > high) {
                    view?.showCreateAlertError(R.string.low_limit_above_high_limit)
                    return
                }

                prefs.firebaseToken
                    ?.let {
                        AlertsRepository.getInstance(prefs).createAlert(
                            AlertData(
                                id            = null,
                                firebaseToken = it,
                                coinId        = coin.id,
                                name          = coin.name,
                                lowLimit      = low,
                                highLimit     = high,
                                createdAt     = JSONDateUtils.toString(Date())
                            )
                        )
                        view?.navigateBack()
                        return
                    }

                view?.showCreateAlertError(R.string.no_firebase_token_message)
                return
            }
        }

        view?.showCreateAlertError(R.string.generic_invalid_data_message)
    }

    fun validateLowLimit(price: String) {
        view?.showLowLimitErrorHint(getValidationMessage(price))
    }

    fun validateHighLimit(price: String) {
        view?.showHighLimitErrorHint(getValidationMessage(price))
    }

    private fun getValidationMessage(price: String): Int? {
        return if (price.replace(',', '.').toDoubleOrNull() != null) {
            null
        } else {
            R.string.generic_invalid_value_message
        }
    }
}