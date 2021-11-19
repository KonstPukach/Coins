package com.oukachkosnt.coins.ui.charts

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.oukachkosnt.coins.R

@SuppressLint("ViewConstructor")
class ChartTextMarker(
    context: Context,
    private val formatter: (Float) -> String
) : MarkerView(context, R.layout.text_chart_marker) {

    private val markerText = findViewById<TextView>(R.id.marker_text)

    override fun refreshContent(e: Entry, highlight: Highlight?) {
        markerText.text = formatter(e.y)
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF =
        MPPointF.getInstance(-width / 2.toFloat(), -height.toFloat())
}