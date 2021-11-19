package com.oukachkosnt.coins.recycler

import android.view.View
import android.view.ViewGroup
import com.oukachkosnt.coins.R
import java.lang.IllegalArgumentException

class EndlessListAdapter<T>(
    layoutSupplier: () -> Int,
    holderSupplier: HolderSupplier<T>,
    itemClickListener: ItemClickListener<T>? = null,
    diffCallbackSupplier: UpdateNotifier<T>? = null,
    initialDataList: List<T> = emptyList()
) : ListAdapter<T>(
    layoutSupplier    = layoutSupplier,
    holderSupplier    = holderSupplier,
    itemClickListener = itemClickListener,
    updateNotifier    = diffCallbackSupplier,
    initialDataList   = initialDataList
) {
    private var isLoading = false

    private fun shouldShowLoading() = isLoading && super.getItemCount() != 0

    fun setLoading(loading: Boolean) {
        if (isLoading == loading) return

        val itemCountBefore = itemCount
        isLoading = loading

        when (itemCountBefore - itemCount) {
            -1 -> notifyItemInserted(itemCountBefore)
            1  -> notifyItemRemoved(itemCount)
            else -> { }
        }
    }

    override fun getItemCount()
        = super.getItemCount().let { if (shouldShowLoading()) it + 1 else it }

    override fun getItemViewType(position: Int): Int
        = if (position == itemCount - 1 && shouldShowLoading()) ITEM_LOADING else ITEM_DATA


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapterViewHolder<T>
        = when(viewType) {
              ITEM_DATA ->    super.onCreateViewHolder(parent, viewType)
              ITEM_LOADING -> LoadingViewHolder(inflateView(R.layout.loading_list_item, parent))
              else ->         throw IllegalArgumentException("unknown view type")
          }

    override fun onBindViewHolder(holder: ListAdapterViewHolder<T>, position: Int) {
        when (holder.itemViewType) {
            ITEM_DATA ->    super.onBindViewHolder(holder, position)
            ITEM_LOADING -> { }
            else ->         throw IllegalArgumentException("unknown view type")
        }
    }

    class LoadingViewHolder<in T>(rootView: View) : ListAdapterViewHolder<T>(rootView) {
        override fun bind(data: T) {
            // ignored
        }
    }

    companion object {
        private const val ITEM_DATA = 42
        private const val ITEM_LOADING = 43
    }
}