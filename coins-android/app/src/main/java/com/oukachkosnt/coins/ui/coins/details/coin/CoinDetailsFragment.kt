package com.oukachkosnt.coins.ui.coins.details.coin

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.oukachkosnt.coins.ui.coins.details.CoinIdProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.data.domain.RealTimePriceData
import com.oukachkosnt.coins.databinding.FragmentCoinDetailsBinding
import com.oukachkosnt.coins.formatters.*
import com.oukachkosnt.coins.mvp.MvpFragment
import com.oukachkosnt.coins.mvp.MvpView
import com.oukachkosnt.coins.repository.CryptoCoinsRepository
import com.oukachkosnt.coins.repository.DbRepository
import com.oukachkosnt.coins.utils.getColorForPriceChange
import kotlin.math.abs

class  CoinDetailsFragment : MvpFragment<CoinDetailsPresenter>(R.layout.fragment_coin_details),
    CoinDetailsView {

    private lateinit var binding: FragmentCoinDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createPresenter(): CoinDetailsPresenter {
        return (activity as CoinIdProvider)
            .getCoinData()
            .let {
                CoinDetailsPresenter(
                    it,
                    CoinDetailsStateRetainer.getInstance(it, childFragmentManager),
                    this
                )
            }
    }

    override fun onStart() {
        super.onStart()
        presenter?.onViewShown()
    }

    override fun onStop() {
        super.onStop()
        presenter?.onViewHidden()
    }

    override fun setData(coin: CryptoCoinData) {
        with(binding) {
            Glide.with(this@CoinDetailsFragment)
                .load(coin.iconUrl)
                .apply(RequestOptions().override(resources.getDimensionPixelSize(R.dimen.coin_icon_size)))
                .into(binding.coinDetailsIcon)

            coinDetailsName.text            = getString(R.string.coin_name_format_str, coin.name, coin.symbol)
            coinDetailsPrice.text           = coin.priceUsd.formatPriceUsd()
            coinDetailsPriceBtc.text        = coin.priceBtc.formatPrice()
            coinDetails24hVolume.text       = coin.volumeUsd24h.formatPriceUsd()
            coinDetailsMarketCap.text       = coin.marketCapitalizationUsd.formatPriceUsd()
            coinDetailsAvailableSupply.text = coin.availableSupply.formatPrice()
            coinDetailsTotalSupply.text     = coin.totalSupply.formatPrice()
            coinDetailsChange1h.text        = abs(coin.percentChange1h).formatPercent()
            coinDetailsChange24h.text       = abs(coin.percentChange24h).formatPercent()
            coinDetailsChange7d.text        = abs(coin.percentChange7d).formatPercent()
            coinDetailsTimestamp.text       = coin.lastUpdated.formatDateTime()

            activity?.let {
                coinDetailsChange1h.setTextColor(it.getColorForPriceChange(coin.percentChange1h))
                coinDetailsChange24h.setTextColor(it.getColorForPriceChange(coin.percentChange24h))
                coinDetailsChange7d.setTextColor(it.getColorForPriceChange(coin.percentChange7d))
            }
        }
    }

    override fun setRealTimePrice(data: RealTimePriceData) {
        showRealTimePriceView()

        binding.coinRealtimePriceTimestamp.text = data.lastUpdated.formatTime()
        binding.coinRealtimePrice.setText(data.price.formatPriceUsdAutoprecision())
    }

    override fun setRealTimePriceError() {
        binding.coinRealtimePrice.visibility = View.INVISIBLE
        binding.coinRealpriceError.visibility = View.VISIBLE
    }

    private fun showRealTimePriceView() {
        binding.coinRealtimePrice.visibility = View.VISIBLE
        binding.coinRealpriceError.visibility = View.INVISIBLE
    }

    override fun setRealTimePriceConnectionState(isConnected: Boolean) {
        binding.coinRealtimePriceDisconnected.isSelected = isConnected
        showRealTimePriceView()
    }

    override fun bindView(rootView: View) {
        binding = FragmentCoinDetailsBinding.bind(rootView)
        initPriceTextSwitcher()
    }

    private fun initPriceTextSwitcher() {
        with(binding.coinRealtimePrice) {
            setFactory { TextView(ContextThemeWrapper(activity, R.style.real_time_price_text_view_style)) }

            inAnimation = AlphaAnimation(0f, 1f).also {
                it.duration = 500
                it.interpolator = DecelerateInterpolator()
            }

            outAnimation = AlphaAnimation(1f, 0f).also {
                it.duration = 500
                it.interpolator = AccelerateInterpolator()
            }
        }
    }

    override fun showError() {
        showSnack(R.string.generic_error_message)
    }
}

interface CoinDetailsView : MvpView {
    fun setData(coin: CryptoCoinData)
    fun showError()
    fun setRealTimePrice(data: RealTimePriceData)
    fun setRealTimePriceError()
    fun setRealTimePriceConnectionState(isConnected: Boolean)
}
