package com.oukachkosnt.coins.ui.coins.base

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oukachkosnt.coins.FloatingActionButtonProvider
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.mvp.list.ListMvpFragment
import com.oukachkosnt.coins.ui.coins.CoinViewHolder
import com.oukachkosnt.coins.ui.coins.getCoinIconsPreloader

abstract class SimpleCoinsListFragment<P: SimpleCoinsListPresenter<out SimpleCoinsListView>>
    : ListMvpFragment<List<CryptoCoinData>, CoinsAdapter, P>(), SimpleCoinsListView {

    final override fun createAdapter() = CoinsAdapter(
        layoutSupplier    = { R.layout.cryptocoin_list_item },
        holderSupplier    = { CoinViewHolder(it, {  }) },
        itemClickListener = { _, data -> /* */ }
    )

    final override fun initRecycler(recycler: RecyclerView) {
        resources.getDimensionPixelSize(R.dimen.margin_small)
                .let { recycler.setPadding(0, it, 0, it) }

        recycler.itemAnimator?.changeDuration = 0
    }

    final override fun onNewDataSet(newData: List<CryptoCoinData>) {
        if (newData.isEmpty()) {
            showEmptyState()
        }

        with(binding.recyclerView) {
            clearOnScrollListeners()
            addOnScrollListener(getCoinIconsPreloader(requireActivity(), newData))
        }
    }

    override fun showFirstFavoriteHelpMessage() {
        showSnack(R.string.first_time_favorite_message)
    }

    override fun showFirstUnfavoriteHelpMessage() {
        showSnack(R.string.first_time_unfavorite_message)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            (activity as? FloatingActionButtonProvider)
                    ?.getActionButton()
                    ?.setOnClickListener {
                        with(binding.recyclerView.layoutManager as LinearLayoutManager) {
                            if (findFirstVisibleItemPosition() < SMOOTH_SCROLL_THRESHOLD) {
                                binding.recyclerView.smoothScrollToPosition(0)
                            } else {
                                binding.recyclerView.scrollToPosition(0)
                            }
                        }
                    }
        }
    }

    companion object {
        private const val SMOOTH_SCROLL_THRESHOLD = 100
    }
}
