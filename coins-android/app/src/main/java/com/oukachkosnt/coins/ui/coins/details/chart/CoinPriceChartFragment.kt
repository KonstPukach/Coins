//package com.oukachkosnt.coins.ui.coins.details.chart
//
//import android.view.View
//import by.maximka.tofico.R
//import by.maximka.tofico.data.domain.TimeInterval
//import by.maximka.tofico.formatters.formatLabel
//import by.maximka.tofico.formatters.formatPriceAutoprecision
//import by.maximka.tofico.mvp.MvpFragment
//import by.maximka.tofico.mvp.MvpView
//import by.maximka.tofico.mvp.list.ViewState
//import by.maximka.tofico.preferences.Preferences
//import by.maximka.tofico.ui.charts.ChartTextMarker
//import by.maximka.tofico.ui.coins.details.chart.CoinPriceChartPresenter
//import com.oukachkosnt.coins.ui.coins.details.CoinIdProvider
//import by.maximka.tofico.ui.dialogs.SelectRealCurrencyDialogFragment
//import by.maximka.tofico.util.interceptHorizontalTouchGesturesFor
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.data.*
//import com.github.mikephil.charting.formatter.IAxisValueFormatter
//import com.oukachkosnt.coins.mvp.MvpFragment
//import kotlinx.android.synthetic.main.activity_coin_details.*
//import kotlinx.android.synthetic.main.fragment_coin_price_chart.*
//import java.util.*
//
//class CoinPriceChartFragment : MvpFragment<CoinPriceChartPresenter>(R.layout.fragment_coin_price_chart),
//    CoinPriceChartView {
//    private lateinit var intervalButtons: Map<TimeInterval, View>
//
//    override fun createPresenter() = CoinPriceChartPresenter((activity as CoinIdProvider).getCoinData(),
//                                                             this,
//                                                             Preferences(activity!!))
//
//    override fun bindView(rootView: View) {
//        swipe_refresh.setOnRefreshListener { presenter?.refresh() }
//
//        with(coin_price_chart) {
//            description = null
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//            legend.isEnabled = false
//            marker = ChartTextMarker(activity!!, { it.toDouble().formatPriceAutoprecision() })
//        }
//
//        intervalButtons = mapOf(TimeInterval.DAY to interval_24h,
//                                TimeInterval.WEEK to interval_week,
//                                TimeInterval.MONTH to interval_month,
//                                TimeInterval.YEAR to interval_year,
//                                TimeInterval.ALL_TIME to interval_all)
//
//        intervalButtons.forEach { e -> e.value.setOnClickListener { presenter?.setChartInterval(e.key) } }
//
//        price_chart_touch_interceptor
//                .interceptHorizontalTouchGesturesFor(coin_price_chart,
//                                                     onGestureStarted = {
//                                                         setInteractingWithChart(true)
//                                                     },
//                                                     onGestureFinished = {
//                                                         setInteractingWithChart(false)
//                                                     })
//
//        coin_price_currency.setOnClickListener {
//            presenter?.getCurrencies()
//                     ?.also {
//                         SelectRealCurrencyDialogFragment
//                                 .newInstance(it, { presenter?.setExchangeCurrency(it) })
//                                 .show(fragmentManager, SELECT_CURRENCY_DIALOG_TAG)
//                     }
//                     ?: showSnack(R.string.currencies_not_ready)
//        }
//
//        val dialog = fragmentManager?.findFragmentByTag(SELECT_CURRENCY_DIALOG_TAG)
//                                    as? SelectRealCurrencyDialogFragment
//        dialog?.callback = { presenter?.setExchangeCurrency(it) }
//
//        setCoinPriceChartViewState(ViewState.SHOW_LOADING)
//    }
//
//    private fun setInteractingWithChart(isInteracting: Boolean) {
//        swipe_refresh.isEnabled = !isInteracting
//        activity!!.view_pager.requestDisallowInterceptTouchEvent(isInteracting)
//    }
//
//    override fun showError() {
//        if (coin_price_loading.visibility == View.VISIBLE) {
//            setCoinPriceChartViewState(ViewState.SHOW_ERROR)
//        } else {
//            showSnack(R.string.generic_error_message)
//        }
//    }
//
//    override fun setData(data: List<Pair<Date, Double>>) {
//        if (data.isEmpty()) {
//            setCoinPriceChartViewState(ViewState.SHOW_ERROR)
//            return
//        }
//
//        val dataSet = data.map { Entry(it.first.time.toFloat(), it.second.toFloat()) }
//                          .let { LineDataSet(it, "Price") }
//                          .also {
//
//                              @Suppress("DEPRECATION")
//                              with(resources.getColor(R.color.colorAccent)) {
//                                  it.color = this
//                                  it.setCircleColor(this)
//                              }
//
//                              it.lineWidth = 2f
//                              it.setDrawCircleHole(false)
//                              it.circleRadius = 1f
//                          }
//
//        with(coin_price_chart.xAxis) {
//            valueFormatter = IAxisValueFormatter { t, _ ->
//                val scale = coin_price_chart.visibleXRange.toLong() / coin_price_chart.xAxis.labelCount
//                return@IAxisValueFormatter Date(t.toLong()).formatLabel(scale)
//            }
//        }
//
//        coin_price_chart.also {
//            it.data = LineData(dataSet)
//            it.invalidate()
//            it.animateX(500)
//        }
//
//        setCoinPriceChartViewState(ViewState.SHOW_CONTENT)
//    }
//
//    override fun setChartPage(interval: TimeInterval) {
//        intervalButtons.values.forEach { it.isSelected = false }
//        intervalButtons.getValue(interval).isSelected = true
//        setCoinPriceChartViewState(ViewState.SHOW_LOADING)
//    }
//
//    override fun setCurrency(currency: Currency) {
//        coin_price_currency.text = currency.name
//    }
//
//    override fun setRefresh(isRefresh: Boolean) {
//        swipe_refresh.isRefreshing = isRefresh
//    }
//
//    private fun setCoinPriceChartViewState(state: ViewState) {
//        fun visibleIf(visibleState: ViewState) = if (state == visibleState) View.VISIBLE else View.INVISIBLE
//
//        coin_price_error.visibility =   visibleIf(ViewState.SHOW_ERROR)
//        coin_price_loading.visibility = visibleIf(ViewState.SHOW_LOADING)
//        coin_price_chart.visibility =   visibleIf(ViewState.SHOW_CONTENT)
//
//        enableSwipeRefreshIfContentLoaded()
//    }
//
//    private fun enableSwipeRefreshIfContentLoaded() {
//        swipe_refresh.isEnabled = coin_price_loading.visibility == View.INVISIBLE
//    }
//
//    companion object {
//        private const val SELECT_CURRENCY_DIALOG_TAG = "selectCurrencyTag"
//    }
//}
//
//interface CoinPriceChartView : MvpView {
//    fun showError()
//    fun setRefresh(isRefresh: Boolean)
//    fun setChartPage(interval: TimeInterval)
//    fun setData(data: List<Pair<Date, Double>>)
//    fun setCurrency(currency: Currency)
//}