package com.oukachkosnt.coins.ui.stats

import com.github.mikephil.charting.data.PieEntry
import com.oukachkosnt.coins.data.domain.MarketStatsData
import com.oukachkosnt.coins.data.domain.TimeInterval
import com.oukachkosnt.coins.mvp.MvpView
import java.util.*

interface MarketStatsView : MvpView {
    fun setGlobalStats(data: MarketStatsData)
    fun setGlobalStatsError()

    fun setCapShareData(pies: List<PieEntry>)
    fun setCapShareError()

    fun setRefresh(isRefresh: Boolean)
}