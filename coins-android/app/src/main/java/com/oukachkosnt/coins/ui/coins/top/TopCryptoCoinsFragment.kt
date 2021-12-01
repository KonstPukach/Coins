package com.oukachkosnt.coins.ui.coins.top

import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.oukachkosnt.coins.FloatingActionButtonProvider
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.TopCoins
import com.oukachkosnt.coins.mvp.list.ListMvpFragment
import com.oukachkosnt.coins.recycler.ListAdapterViewHolder
import com.oukachkosnt.coins.recycler.bind.Binder
import com.oukachkosnt.coins.recycler.bind.BinderAdapter
import com.oukachkosnt.coins.ui.coins.CoinViewHolder
import com.oukachkosnt.coins.ui.coins.details.CoinDetailsPagesActivityDirections

class TopCryptoCoinsFragment :
    ListMvpFragment<TopCoins, BinderAdapter<TopCoins>, TopCryptoCoinsPresenter>(),
    TopCryptoCoinsView {

    override fun createPresenter() = TopCryptoCoinsPresenter(this, null)

    override fun createAdapter(): BinderAdapter<TopCoins> = BinderAdapter({ it.toBinders() })

    override fun initRecycler(recycler: RecyclerView) {
        recycler.setPadding(0, 0, 0, resources.getDimensionPixelSize(R.dimen.margin_small))
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            (activity as? FloatingActionButtonProvider)
                ?.getActionButton()
                ?.setOnClickListener { binding.recyclerView.smoothScrollToPosition(0) }
        }
    }

    override fun showFirstFavoriteHelpMessage() {
        showSnack(R.string.first_time_favorite_message)
    }

    override fun showFirstUnfavoriteHelpMessage() {
        showSnack(R.string.first_time_unfavorite_message)
    }

    private fun TopCoins.toBinders(): List<Binder<*, *>> {
        val binders = mutableListOf<Binder<*, *>>()
        val coinsViewHolderSupplier =
            { v: View -> CoinViewHolder(v, { presenter?.switchCoinFavorite(it) }) }
        val titleViewHolderSupplier = { v: View -> SectionTitleViewHolder(v) }

        listOf(
            getString(R.string.popular_section_title) to popular,
            getString(R.string.gainers_section_title) to gainers,
            getString(R.string.losers_secction_title)  to losers
        )

            .forEach { (title, list) ->
                binders.add(
                    Binder(
                        R.layout.section_title_list_item,
                        titleViewHolderSupplier,
                        title
                    )
                )
                binders.addAll(list.map {
                    Binder(R.layout.cryptocoin_list_item,
                    coinsViewHolderSupplier,
                    it,
                    {
                        findNavController().navigate(
                            CoinDetailsPagesActivityDirections.actionNavCryptoCoinsToNavCryptoCoinDetails(it)
                        )
                    })
                })
            }

        return binders
    }

    private class SectionTitleViewHolder(root: View) : ListAdapterViewHolder<String>(root) {
        override fun bind(data: String) {
            itemView.findViewById<TextView>(R.id.section_title).text = data
        }
    }
}