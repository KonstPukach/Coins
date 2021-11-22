package com.oukachkosnt.coins.ui.converter

import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.ExchangeRate
import com.oukachkosnt.coins.databinding.FragmentConverterBinding
import com.oukachkosnt.coins.formatters.formatDecimal
import com.oukachkosnt.coins.mvp.MvpFragment
import com.oukachkosnt.coins.mvp.list.ViewState
import com.oukachkosnt.coins.ui.dialogs.SelectCurrencyDialogFragment
import com.oukachkosnt.coins.utils.addTextChangedListener

class ConverterFragment : MvpFragment<ConverterPresenter>(R.layout.fragment_converter),
    ConverterView {
    companion object {
        private const val SELECT_CURRENCY_DIALOG_TAG = "select_currency_tag"
    }

    private lateinit var binding: FragmentConverterBinding

    override fun createPresenter() = ConverterPresenter(this)

    override fun bindView(rootView: View) {
        binding = FragmentConverterBinding.bind(rootView)

        binding.converterSwap.setOnClickListener { presenter?.swapCurrencies() }
        binding.swipeRefresh.setOnRefreshListener { presenter?.refresh() }

        val activeDialog = fragmentManager?.findFragmentByTag(SELECT_CURRENCY_DIALOG_TAG)
                                          as? SelectCurrencyDialogFragment
        activeDialog?.presenterProvider = { presenter }

        binding.converterBaseCurrency.setOnClickListener {
            SelectCurrencyDialogFragment
                    .createForBaseCurrency(presenter)
                    .show(parentFragmentManager, SELECT_CURRENCY_DIALOG_TAG)
        }

        binding.converterTargetCurrency.setOnClickListener {
            SelectCurrencyDialogFragment
                    .createForTargetCurrency(presenter)
                    .show(parentFragmentManager, SELECT_CURRENCY_DIALOG_TAG)
        }

        binding.converterBaseValueEdit.addTextChangedListener { presenter?.updateBaseCurrencyValue(it) }
        binding.converterTargetValueEdit.addTextChangedListener { presenter?.updateTargetCurrencyValue(it) }

        setScreenState(ViewState.SHOW_LOADING)
    }

    override fun showContent() {
        setScreenState(ViewState.SHOW_CONTENT)
    }

    override fun showError() {
        if (binding.converterLoading.visibility == View.VISIBLE) {
            setScreenState(ViewState.SHOW_ERROR)
        } else {
            showSnack(R.string.generic_error_message)
        }
    }

    override fun setRefreshing(isRefresh: Boolean) {
        binding.swipeRefresh.isRefreshing = isRefresh
    }

    override fun setExchangeRate(exchangeRate: ExchangeRate) {
        with (binding) {
            converterBaseCurrency.text = exchangeRate.baseCurrency.name
            converterTargetCurrency.text = exchangeRate.targetCurrency.name
            converterRate.text = getString(
                R.string.exchange_rate_format_str,
                exchangeRate.rate.formatDecimal()
            )

            converterBaseValueLayout.hint = exchangeRate.baseCurrency.name
            converterTargetValueLayout.hint = exchangeRate.targetCurrency.name
        }
    }

    override fun setBaseCurrency(value: Double) {
        binding.converterBaseValueEdit.setCurrencyValue(value)
        binding.converterBaseValueLayout.setError(false)
    }

    override fun setTargetCurrency(value: Double) {
        binding.converterTargetValueEdit.setCurrencyValue(value)
        binding.converterTargetValueLayout.setError(false)
    }

    override fun setBaseCurrencyError(isError: Boolean) {
        binding.converterBaseValueLayout.setError(isError)
    }

    override fun setTargetCurrencyError(isError: Boolean) {
        binding.converterTargetValueLayout.setError(isError)
    }

    private fun TextInputEditText.setCurrencyValue(value: Double) {
        setText(value.formatDecimal())

        // move cursor to the end
        setSelection(text?.length ?: 0)
    }

    private fun TextInputLayout.setError(isError: Boolean) {
        isErrorEnabled = isError
        error = if (isError) getString(R.string.currency_invalid_value) else null
    }

    private fun setScreenState(state: ViewState) {
        fun visibleIf(visibleState: ViewState) = if (state == visibleState) View.VISIBLE else View.INVISIBLE

        with (binding) {
            binding.converterError.visibility = visibleIf(ViewState.SHOW_ERROR)
            binding.converterLoading.visibility = visibleIf(ViewState.SHOW_LOADING)
            binding.converterContent.visibility = visibleIf(ViewState.SHOW_CONTENT)
        }

        enableSwipeRefreshIfContentLoaded()
    }

    private fun enableSwipeRefreshIfContentLoaded() {
        binding.swipeRefresh.isEnabled = binding.converterLoading.visibility == View.INVISIBLE
    }
}
