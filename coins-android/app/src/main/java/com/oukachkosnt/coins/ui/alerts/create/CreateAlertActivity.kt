package com.oukachkosnt.coins.ui.alerts.create

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.oukachkosnt.coins.Preferences
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.databinding.ActivityCreateAlertBinding
import com.oukachkosnt.coins.formatters.formatEditAlertPrice
import com.oukachkosnt.coins.formatters.formatPriceUsdAutoprecision
import com.oukachkosnt.coins.mvp.MvpActivity
import com.oukachkosnt.coins.mvp.list.ViewState
import com.oukachkosnt.coins.ui.dialogs.SelectCoinDialogFragment
import com.oukachkosnt.coins.utils.addTextChangedListener

class CreateAlertActivity : MvpActivity<CreateAlertPresenter>(R.layout.activity_create_alert),
    CreateAlertView {

    private lateinit var binding: ActivityCreateAlertBinding

    override fun createPresenter() = CreateAlertPresenter(this, Preferences(this))

    override fun bindView() {
        binding = ActivityCreateAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnCreateAlert.setOnClickListener {
            presenter?.createAlert(
                binding.createAlertLowLimitEdit.getTextAsString(),
                binding.createAlertHighLimitEdit.getTextAsString()
            )
        }

        binding.createAlertCoin.setOnClickListener {
            presenter?.let {
                SelectCoinDialogFragment
                    .newInstance(it)
                    .show(supportFragmentManager, COIN_DIALOG_TAG)
            }
        }

        binding.createAlertHighLimitEdit.addTextChangedListener { presenter?.validateHighLimit(it) }
        binding.createAlertLowLimitEdit.addTextChangedListener { presenter?.validateLowLimit(it) }

        // Disable hint animation to prevent lags on navigation to this screen
        binding.createAlertLowLimit.isHintAnimationEnabled = false
        binding.createAlertHighLimit.isHintAnimationEnabled = false

        setViewState(ViewState.SHOW_LOADING)
    }

    override fun onPresenterInitialized() {
        val dialog = supportFragmentManager
                        .findFragmentByTag(COIN_DIALOG_TAG)
                        as? SelectCoinDialogFragment

        if (dialog != null) {
            presenter?.let { dialog.presenter = it }
        }

        val preselectedCoin = intent.extras?.getSerializable(COIN_DATA_KEY) as? CryptoCoinData
        preselectedCoin?.let {
            intent.extras?.putSerializable(COIN_DATA_KEY, null)    // consume coin once
            presenter?.setCurrentCoin(it)
        }
    }

    override fun showErrorState() {
        setViewState(ViewState.SHOW_ERROR)
    }

    override fun showCreateAlertError(messageId: Int) {
        showSnack(binding.contentView, messageId)
    }

    override fun showLowLimitErrorHint(messageId: Int?) {
        binding.createAlertLowLimit.error = messageId?.let { getString(it) }
        binding.createAlertLowLimit.isErrorEnabled = messageId != null
    }

    override fun showHighLimitErrorHint(messageId: Int?) {
        binding.createAlertHighLimit.error = messageId?.let { getString(it) }
        binding.createAlertHighLimit.isErrorEnabled = messageId != null
    }

    override fun setCoin(coin: CryptoCoinData) {
        binding.createAlertCoin.text = coin.name
        binding.createAlertCoinPrice.text = getString(
            R.string.current_price_format_str,
            coin.priceUsd.formatPriceUsdAutoprecision()
        )

        setViewState(ViewState.SHOW_CONTENT)
    }

    override fun setLowLimit(price: Double) {
        binding.createAlertLowLimitEdit.setText(price.formatEditAlertPrice())
    }

    override fun setHighLimit(price: Double) {
        binding.createAlertHighLimitEdit.setText(price.formatEditAlertPrice())
    }

    override fun navigateBack() {
        onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setViewState(state: ViewState) {
        fun visibleIf(visibleState: ViewState) = if (state == visibleState) View.VISIBLE else View.INVISIBLE

        binding.errorView.visibility   = visibleIf(ViewState.SHOW_ERROR)
        binding.loadingView.visibility = visibleIf(ViewState.SHOW_LOADING)
        binding.contentView.visibility = visibleIf(ViewState.SHOW_CONTENT)
    }

    private fun TextInputEditText.getTextAsString() = text?.toString() ?: ""

    companion object {
        private const val COIN_DIALOG_TAG = "coinDialogTag"
        private const val COIN_DATA_KEY = "coinData"
        private const val HELP_DIALOG_TAG = "helpDialogTag"

        fun makeArgs(coin: CryptoCoinData) = Bundle().also { it.putSerializable(COIN_DATA_KEY, coin) }
    }
}
