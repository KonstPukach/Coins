package com.oukachkosnt.coins.ui.stats

import android.content.res.Configuration
import android.graphics.Color
import android.view.View
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.oukachkosnt.coins.CollapsingToolbarOwner
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.MarketStatsData
import com.oukachkosnt.coins.databinding.FragmentMarketStatsBinding
import com.oukachkosnt.coins.formatters.formatDateTime
import com.oukachkosnt.coins.formatters.formatPercent
import com.oukachkosnt.coins.formatters.formatPriceUsd
import com.oukachkosnt.coins.mvp.MvpFragment
import com.oukachkosnt.coins.mvp.list.ViewState

class MarketStatsFragment : MvpFragment<MarketStatsPresenter>(R.layout.fragment_market_stats),
    MarketStatsView {

    private lateinit var binding: FragmentMarketStatsBinding

    override fun createPresenter() = MarketStatsPresenter(this)

    override fun bindView(rootView: View) {
        binding = FragmentMarketStatsBinding.bind(rootView)

        binding.swipeRefresh.setOnRefreshListener { presenter?.refresh() }

        with(binding.shareDiagram) {
            description = null
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        }

        setGlobalStatsViewState(ViewState.SHOW_LOADING)
        setCapShareViewState(ViewState.SHOW_LOADING)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            (activity as? CollapsingToolbarOwner)?.enableCollapsingToolbar(true)
        }
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

    private fun enableSwipeRefreshIfContentLoaded() {
        binding.swipeRefresh.isEnabled =
            binding.globalStatsLoading.visibility == View.INVISIBLE
                    && binding.shareLoading.visibility == View.INVISIBLE
    }
}
