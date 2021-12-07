package com.oukachkosnt.coins.ui.stats

import com.github.mikephil.charting.data.PieEntry
import com.oukachkosnt.coins.data.domain.CapShareData
import com.oukachkosnt.coins.mvp.MvpPresenter
import com.oukachkosnt.coins.repository.GlobalMarketStatsRepository

class MarketStatsPresenter(
    view: MarketStatsView
) : MvpPresenter<MarketStatsView>(view) {

    override fun init() {
        addSubscription(
            GlobalMarketStatsRepository
                .subscribeOnGlobalStats(
                    { view?.setGlobalStats(it) },
                    { view?.setGlobalStatsError() }
                )
        )

        addSubscription(
            GlobalMarketStatsRepository
                .subscribeOnResetState { view?.setRefresh(it) }
        )

        addSubscription(
            GlobalMarketStatsRepository
                .subscribeOnMarketCapDiagramData({ view?.setCapShareData(getSharePieData(it)) },
                    { view?.setCapShareError() })
        )
    }

    fun refresh() {
        GlobalMarketStatsRepository.reset()
    }


    private fun getSharePieData(data: List<CapShareData>): List<PieEntry> =
        data.map { PieEntry(it.share.toFloat(), it.name) }
}