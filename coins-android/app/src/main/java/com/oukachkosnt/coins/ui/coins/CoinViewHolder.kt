package com.oukachkosnt.coins.ui.coins

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import com.bumptech.glide.util.FixedPreloadSizeProvider
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.request.RequestOptions
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.formatters.*
import com.oukachkosnt.coins.recycler.ListAdapterViewHolder
import com.oukachkosnt.coins.utils.getColorForPriceChange
import kotlin.math.abs

class CoinViewHolder(
    rootView: View,
    private val onFavoriteClick: (CryptoCoinData) -> Unit
) : ListAdapterViewHolder<CryptoCoinData>(rootView) {

    private val nameView: TextView        = rootView.findViewById(R.id.coin_name)
    private val priceView: TextView       = rootView.findViewById(R.id.coin_price)
    private val lastUpdatedView: TextView = rootView.findViewById(R.id.coin_last_updated)
    private val iconView: ImageView       = rootView.findViewById(R.id.coin_icon)
    private val favoriteView: View        = rootView.findViewById(R.id.coin_favorite)
    private val iconSize                  = rootView.resources.getDimensionPixelSize(R.dimen.coin_icon_size)

    override fun bind(data: CryptoCoinData) {
        nameView.text           = data.name
        priceView.text          = buildPrice(data.priceUsd, data.percentChange24h)
        lastUpdatedView.text    = data.lastUpdated.formatDateTime()
        favoriteView.isSelected = data.isFavorite

        favoriteView.setOnClickListener {
            onFavoriteClick(data)
        }

        Glide.with(iconView)
            .load(data.iconUrl)
            .apply(RequestOptions().override(iconSize))
            .into(iconView)
    }

    private fun buildPrice(price: Double, change: Double): Spannable {
        fun getChangeArrow() = when {
            change > 0 -> "\u25B2"
            change < 0 -> "\u25BC"
            else       -> ""
        }

        val context = itemView.context
        val asString = context.getString(
            R.string.coin_item_price_format_str,
            price.formatPriceUsd(),
            getChangeArrow() + abs(change).formatPercent())

        return SpannableString(asString).also {
            it.setSpan(
                ForegroundColorSpan(context.getColorForPriceChange(change)),
                asString.indexOf('(') + 1,
                asString.indexOf(')'),
                0
            )
        }
    }
}

fun getCoinIconsPreloader(context: Context, data: List<CryptoCoinData>): RecyclerViewPreloader<CryptoCoinData> {
    val iconSize = context.resources.getDimensionPixelSize(R.dimen.coin_icon_size)

    return RecyclerViewPreloader(
        Glide.with(context),
        CoinIconPreloadProvider(context, data, iconSize),
        FixedPreloadSizeProvider(iconSize, iconSize),
       12
    )
}

private class CoinIconPreloadProvider(
    private val context: Context,
    private val data: List<CryptoCoinData>,
    private val overrideSize: Int
) : PreloadModelProvider<CryptoCoinData> {

    override fun getPreloadItems(position: Int) = data.getOrNull(position)?.let { listOf(it) }.orEmpty()

    override fun getPreloadRequestBuilder(item: CryptoCoinData): RequestBuilder<*> {
        return Glide
            .with(context)
            .load(item.iconUrl)
            .apply(RequestOptions().override(overrideSize))
    }
}