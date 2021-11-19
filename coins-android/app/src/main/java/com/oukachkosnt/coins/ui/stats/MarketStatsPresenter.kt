package com.oukachkosnt.coins.ui.stats

import com.github.mikephil.charting.data.PieEntry
import com.oukachkosnt.coins.Preferences
import com.oukachkosnt.coins.data.domain.CapShareData
import com.oukachkosnt.coins.data.domain.TimeInterval
import com.oukachkosnt.coins.mvp.MvpPresenter
import com.oukachkosnt.coins.repository.GlobalMarketStatsRepository
import io.reactivex.disposables.Disposable

class MarketStatsPresenter(
    view: MarketStatsView,
    private val prefs: Preferences
) : MvpPresenter<MarketStatsView>(view) {
    private var capHistorySubscription: Disposable? = null

    override fun init() {
        view?.setCapHistoryChartPage(prefs.marketCapShareTimeInterval)

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

        subscribeOnCapHistory()
    }

    override fun onViewDestroyed() {
        capHistorySubscription?.dispose()
        super.onViewDestroyed()
    }

    fun refresh() {
        GlobalMarketStatsRepository.reset()
    }

    fun setCapHistoryInterval(interval: TimeInterval) {
        if (interval == prefs.marketCapShareTimeInterval) return

        capHistorySubscription?.dispose()

        prefs.marketCapShareTimeInterval = interval
        view?.setCapHistoryChartPage(interval)

        subscribeOnCapHistory()
    }

    private fun subscribeOnCapHistory() {
        val capTimeInterval = prefs.marketCapShareTimeInterval

        capHistorySubscription =
            GlobalMarketStatsRepository
                .subscribeOnCapHistory(capTimeInterval,
                    { view?.setCapHistoryShareDate(it, capTimeInterval) },
                    { view?.setCapHistoryShareError() })

        GlobalMarketStatsRepository.loadCapHistoryDataIfMissed(capTimeInterval)
    }

    private fun getSharePieData(data: List<CapShareData>): List<PieEntry> =
        data.map { PieEntry(it.share.toFloat(), it.name) }
}