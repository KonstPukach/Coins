package com.oukachkosnt.coins.ui.stats

import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.oukachkosnt.coins.CollapsingToolbarOwner
import com.oukachkosnt.coins.Preferences
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.MarketStatsData
import com.oukachkosnt.coins.data.domain.TimeInterval
import com.oukachkosnt.coins.databinding.FragmentMarketStatsBinding
import com.oukachkosnt.coins.formatters.formatDateTime
import com.oukachkosnt.coins.formatters.formatLabel
import com.oukachkosnt.coins.formatters.formatPercent
import com.oukachkosnt.coins.formatters.formatPriceUsd
import com.oukachkosnt.coins.mvp.MvpFragment
import com.oukachkosnt.coins.mvp.list.ViewState
import com.oukachkosnt.coins.ui.charts.ChartTextMarker
import com.oukachkosnt.coins.utils.interceptHorizontalTouchGesturesFor
import java.util.*

class MarketStatsFragment : MvpFragment<MarketStatsPresenter>(R.layout.fragment_market_stats),
    MarketStatsView {
    private lateinit var intervalButtons: Map<TimeInterval, View>

    private lateinit var binding: FragmentMarketStatsBinding

    override fun createPresenter() = MarketStatsPresenter(this, Preferences(requireActivity()))

    override fun bindView(rootView: View) {
        binding = FragmentMarketStatsBinding.bind(rootView)

        binding.swipeRefresh.setOnRefreshListener { presenter?.refresh() }

        with(binding.shareDiagram) {
            description = null
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        }

        with(binding.shareHistoryChart) {
            description = null
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisLeft.valueFormatter = PercentFormatter()
            axisRight.valueFormatter = PercentFormatter()
            marker = ChartTextMarker(requireActivity()) { it.toDouble().formatPercent() }
        }

        setGlobalStatsViewState(ViewState.SHOW_LOADING)
        setCapShareViewState(ViewState.SHOW_LOADING)
        setCapShareHistoryViewState(ViewState.SHOW_LOADING)

        intervalButtons = mapOf(
            TimeInterval.DAY      to binding.historyShare24h,
            TimeInterval.MONTH    to binding.historyShareMonth,
            TimeInterval.YEAR     to binding.historyShareYear,
            TimeInterval.ALL_TIME to binding.historyShareAll
        )

        intervalButtons.forEach { e ->
            e.value.setOnClickListener {
                presenter?.setCapHistoryInterval(
                    e.key
                )
            }
        }

        protectFromTouchTheft(binding.shareHistoryChartTouchInterceptor, binding.shareHistoryChart)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            (activity as? CollapsingToolbarOwner)?.enableCollapsingToolbar(true)
        }
    }

    private fun protectFromTouchTheft(touchInterceptor: View, guardedView: View) {

        fun setInteractingWithGuardedView(isInteracting: Boolean) {
            binding.swipeRefresh.isEnabled = !isInteracting
            touchInterceptor.parent.requestDisallowInterceptTouchEvent(isInteracting)
        }

        touchInterceptor.interceptHorizontalTouchGesturesFor(
            guardedView,
            onGestureStarted = {
                setInteractingWithGuardedView(true)
            },
            onGestureFinished = {
                setInteractingWithGuardedView(false)
            }
        )
    }

    override fun setGlobalStats(data: MarketStatsData) {
        with (binding) {
            globalStatsTotalCap.text          = data.totalMarketCapUsd.formatPriceUsd()
            globalStatsTotal24hVolume.text    = data.total24hVolumeUsd.formatPriceUsd()
            globalStatsBitcoinPercentage.text = data.bitcoinPartOfCap.formatPercent()
            globalStatsActiveCurrencies.text  = data.activeCurrencies.toString()
            globalStatsActiveAssets.text      = data.activeAssets.toString()
            globalStatsActiveMarkets.text     = data.activeMarkets.toString()
            globalStatsTimestamp.text         = data.timestamp.formatDateTime()
        }

        setGlobalStatsViewState(ViewState.SHOW_CONTENT)
    }

    override fun setCapShareData(pies: List<PieEntry>) {
        val dataSet = PieDataSet(pies, "")
            .also {
                it.setColors(*(ColorTemplate.MATERIAL_COLORS + ColorTemplate.LIBERTY_COLORS))
                it.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                it.valueFormatter = PercentFormatter()
            }

        binding.shareDiagram.data = PieData(dataSet)
        binding.shareDiagram.invalidate()
        binding.shareDiagram.animateY(500)

        setCapShareViewState(ViewState.SHOW_CONTENT)
    }

    override fun setCapHistoryShareDate(
        data: Map<String, List<Pair<Date, Double>>>,
        interval: TimeInterval
    ) {
        val dataSets = data.mapValues {
            it.value.map { Entry(it.first.time.toFloat(), it.second.toFloat()) }
        }.map { LineDataSet(it.value,
            it.key.replace('-', ' ')
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault())
                    else it.toString()
                }
        )}

        val colors =
            ColorTemplate.MATERIAL_COLORS + ColorTemplate.LIBERTY_COLORS + ColorTemplate.COLORFUL_COLORS
        dataSets.forEachIndexed { i, set ->
            set.color = colors[i % colors.size]
            set.setDrawCircles(false)
        }

        with(binding.shareHistoryChart.xAxis) {
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val scale = binding.shareHistoryChart.visibleXRange.toLong() /
                                binding.shareHistoryChart.xAxis.labelCount
                    return Date(value.toLong()).formatLabel(scale)
                }
            }
        }

        binding.shareHistoryChart.data = LineData(dataSets)
        binding.shareHistoryChart.invalidate()
        binding.shareHistoryChart.animateX(500)

        setCapShareHistoryViewState(ViewState.SHOW_CONTENT)
    }

    override fun setCapHistoryChartPage(interval: TimeInterval) {
        intervalButtons.values.forEach { it.isSelected = false }
        intervalButtons.getValue(interval).isSelected = true
        setCapShareHistoryViewState(ViewState.SHOW_LOADING)
    }

    override fun setRefresh(isRefresh: Boolean) {
        binding.swipeRefresh.isRefreshing = isRefresh
    }

    override fun setGlobalStatsError() {
        if (binding.globalStatsLoading.visibility == View.VISIBLE) {
            setGlobalStatsViewState(ViewState.SHOW_ERROR)
        } else {
            showSnack(R.string.generic_error_message)
        }
    }

    override fun setCapShareError() {
        if (binding.shareLoading.visibility == View.VISIBLE) {
            setCapShareViewState(ViewState.SHOW_ERROR)
        } else {
            showSnack(R.string.generic_error_message)
        }
    }

    override fun setCapHistoryShareError() {
        if (binding.historyShareLoading.visibility == View.VISIBLE) {
            setCapShareHistoryViewState(ViewState.SHOW_ERROR)
        } else {
            showSnack(R.string.generic_error_message)
        }
    }

    private fun setGlobalStatsViewState(state: ViewState) {
        fun visibleIf(visibleState: ViewState) =
            if (state == visibleState) View.VISIBLE else View.INVISIBLE

        binding.globalStatsError.visibility   = visibleIf(ViewState.SHOW_ERROR)
        binding.globalStatsLoading.visibility = visibleIf(ViewState.SHOW_LOADING)
        binding.globalStatsContent.visibility = visibleIf(ViewState.SHOW_CONTENT)

        enableSwipeRefreshIfContentLoaded()
    }

    private fun setCapShareViewState(state: ViewState) {
        fun visibleIf(visibleState: ViewState) =
            if (state == visibleState) View.VISIBLE else View.INVISIBLE

        binding.shareError.visibility   = visibleIf(ViewState.SHOW_ERROR)
        binding.shareLoading.visibility = visibleIf(ViewState.SHOW_LOADING)
        binding.shareDiagram.visibility = visibleIf(ViewState.SHOW_CONTENT)

        enableSwipeRefreshIfContentLoaded()
    }

    private fun setCapShareHistoryViewState(state: ViewState) {
        fun visibleIf(visibleState: ViewState) =
            if (state == visibleState) View.VISIBLE else View.INVISIBLE

        binding.historyShareError.visibility   = visibleIf(ViewState.SHOW_ERROR)
        binding.historyShareLoading.visibility = visibleIf(ViewState.SHOW_LOADING)
        binding.shareHistoryChart.visibility   = visibleIf(ViewState.SHOW_CONTENT)

        enableSwipeRefreshIfContentLoaded()
    }

    private fun enableSwipeRefreshIfContentLoaded() {
        binding.swipeRefresh.isEnabled =
            binding.globalStatsLoading.visibility == View.INVISIBLE
                    && binding.shareLoading.visibility == View.INVISIBLE
                    && binding.historyShareLoading.visibility == View.INVISIBLE
    }
}
