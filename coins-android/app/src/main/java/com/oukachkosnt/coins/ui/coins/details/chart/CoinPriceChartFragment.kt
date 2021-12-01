package com.oukachkosnt.coins.ui.coins.details.chart

import android.view.View
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.oukachkosnt.coins.Preferences
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.Currency
import com.oukachkosnt.coins.data.domain.TimeInterval
import com.oukachkosnt.coins.databinding.FragmentCoinPriceChartBinding
import com.oukachkosnt.coins.formatters.*
import com.oukachkosnt.coins.mvp.MvpFragment
import com.oukachkosnt.coins.mvp.MvpView
import com.oukachkosnt.coins.mvp.list.ViewState
import com.oukachkosnt.coins.ui.charts.ChartTextMarker
import com.oukachkosnt.coins.ui.coins.details.CoinIdProvider
import com.oukachkosnt.coins.ui.dialogs.SelectRealCurrencyDialogFragment
import com.oukachkosnt.coins.utils.interceptHorizontalTouchGesturesFor
import java.util.*

class CoinPriceChartFragment :
    MvpFragment<CoinPriceChartPresenter>(R.layout.fragment_coin_price_chart),
    CoinPriceChartView {
    private lateinit var intervalButtons: Map<TimeInterval, View>

    private lateinit var binding: FragmentCoinPriceChartBinding

    override fun createPresenter() = CoinPriceChartPresenter(
        (activity as CoinIdProvider).getCoinData(),
        this,
        Preferences(requireActivity())
    )

    override fun bindView(rootView: View) {
        binding = FragmentCoinPriceChartBinding.bind(rootView)
        binding.swipeRefresh.setOnRefreshListener { presenter?.refresh() }

        with(binding.coinPriceChart) {
            description = null
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            legend.isEnabled = false
            marker = ChartTextMarker(requireActivity()) { it.toDouble().formatPriceAutoprecision() }
        }

        intervalButtons = mapOf(
            TimeInterval.DAY      to binding.interval24h,
            TimeInterval.WEEK     to binding.intervalWeek,
            TimeInterval.MONTH    to binding.intervalMonth,
            TimeInterval.YEAR     to binding.intervalYear,
            TimeInterval.ALL_TIME to binding.intervalAll
        )

        intervalButtons.forEach { e -> e.value.setOnClickListener { presenter?.setChartInterval(e.key) } }

        binding.priceChartTouchInterceptor
            .interceptHorizontalTouchGesturesFor(binding.coinPriceChart,
                onGestureStarted = {
                    setInteractingWithChart(true)
                },
                onGestureFinished = {
                    setInteractingWithChart(false)
                })

        binding.coinPriceCurrency.setOnClickListener {
            presenter?.getCurrencies()
                ?.also {
                    SelectRealCurrencyDialogFragment
                        .newInstance(it) { presenter?.setExchangeCurrency(it) }
                        .show(parentFragmentManager, SELECT_CURRENCY_DIALOG_TAG)
                }
                ?: showSnack(R.string.currencies_not_ready)
        }

        val dialog = parentFragmentManager.findFragmentByTag(SELECT_CURRENCY_DIALOG_TAG)
                as? SelectRealCurrencyDialogFragment
        dialog?.callback = { presenter?.setExchangeCurrency(it) }

        setCoinPriceChartViewState(ViewState.SHOW_LOADING)
    }

    private fun setInteractingWithChart(isInteracting: Boolean) {
        binding.swipeRefresh.isEnabled = !isInteracting
        requireActivity().findViewById<ViewPager>(R.id.view_pager)
            .requestDisallowInterceptTouchEvent(isInteracting)
    }

    override fun showError() {
        if (binding.coinPriceLoading.isVisible) {
            setCoinPriceChartViewState(ViewState.SHOW_ERROR)
        } else {
            showSnack(R.string.generic_error_message)
        }
    }

    override fun setData(data: List<Pair<Date, Double>>) {
        if (data.isEmpty()) {
            setCoinPriceChartViewState(ViewState.SHOW_ERROR)
            return
        }

        val dataSet = data.map { Entry(it.first.time.toFloat(), it.second.toFloat()) }
            .let { LineDataSet(it, "Price") }
            .also {

                @Suppress("DEPRECATION")
                with(resources.getColor(R.color.colorAccent)) {
                    it.color = this
                    it.setCircleColor(this)
                }

                it.lineWidth = 2f
                it.setDrawCircleHole(false)
                it.circleRadius = 1f
            }

        with(binding.coinPriceChart.xAxis) {
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val scale = binding.coinPriceChart.visibleXRange.toLong() /
                            binding.coinPriceChart.xAxis.labelCount
                    return Date(value.toLong()).formatLabel(scale)
                }
            }
        }

        binding.coinPriceChart.also {
            it.data = LineData(dataSet)
            it.invalidate()
            it.animateX(500)
        }

        setCoinPriceChartViewState(ViewState.SHOW_CONTENT)
    }

    override fun setChartPage(interval: TimeInterval) {
        intervalButtons.values.forEach { it.isSelected = false }
        intervalButtons.getValue(interval).isSelected = true
        setCoinPriceChartViewState(ViewState.SHOW_LOADING)
    }

    override fun setCurrency(currency: Currency) {
        binding.coinPriceCurrency.text = currency.name
    }

    override fun setRefresh(isRefresh: Boolean) {
        binding.swipeRefresh.isRefreshing = isRefresh
    }

    private fun setCoinPriceChartViewState(state: ViewState) {
        fun visibleIf(visibleState: ViewState) =
            if (state == visibleState) View.VISIBLE else View.INVISIBLE

        binding.coinPriceError.visibility = visibleIf(ViewState.SHOW_ERROR)
        binding.coinPriceLoading.visibility = visibleIf(ViewState.SHOW_LOADING)
        binding.coinPriceChart.visibility = visibleIf(ViewState.SHOW_CONTENT)

        enableSwipeRefreshIfContentLoaded()
    }

    private fun enableSwipeRefreshIfContentLoaded() {
        binding.swipeRefresh.isEnabled = binding.coinPriceLoading.visibility == View.INVISIBLE
    }

    companion object {
        private const val SELECT_CURRENCY_DIALOG_TAG = "selectCurrencyTag"
    }
}

interface CoinPriceChartView : MvpView {
    fun showError()
    fun setRefresh(isRefresh: Boolean)
    fun setChartPage(interval: TimeInterval)
    fun setData(data: List<Pair<Date, Double>>)
    fun setCurrency(currency: Currency)
}