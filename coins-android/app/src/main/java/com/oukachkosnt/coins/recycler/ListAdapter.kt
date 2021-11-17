package com.oukachkosnt.coins.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

open class ListAdapter<T>(
    private val layoutSupplier: () -> Int,
    private val holderSupplier: HolderSupplier<T>,
    private val itemClickListener: ItemClickListener<T>? = null,
    private val updateNotifier: UpdateNotifier<T>? = null,
    initialDataList: List<T> = emptyList()
) : RecyclerView.Adapter<ListAdapterViewHolder<T>>(), UpdatableAdapter<List<T>> {

    protected var data: List<T> = ArrayList(initialDataList)

    @SuppressLint("NotifyDataSetChanged")
    override fun setDataList(newData: List<T>) {
        data = newData
        updateNotifier?.also {
            it(data, newData, this)
        } ?: notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapterViewHolder<T> {
        val itemView = inflateView(layoutSupplier(), parent)
        return holderSupplier(itemView)
    }

    protected fun inflateView(@LayoutRes layoutResId: Int, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
    }

    override fun onBindViewHolder(holder: ListAdapterViewHolder<T>, position: Int) {
        val data = data[position]
        with(holder) {
            bind(data)
            setItemClickListener { itemClickListener?.invoke(position, data) }
        }
    }

    override fun getItemCount() = data.size
}

open class ListAdapterWithStableIds<T>(
    layoutSupplier: () -> Int,
    holderSupplier: HolderSupplier<T>,
    itemClickListener: ItemClickListener<T>,
    private val getStringId: T.() -> String
) : ListAdapter<T>(layoutSupplier, holderSupplier, itemClickListener) {

    private val idMapper = IdMapper()

    init {
        setHasStableIds(true)
    }

    final override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int) = data[position].let { idMapper.mapId(it.getStringId()) }

    private class IdMapper {
        private var nextId = 0L
        private val knownIds = mutableMapOf<String, Long>()

        fun mapId(coinId: String): Long {
            knownIds[coinId]?.also { return it }

            knownIds[coinId] = nextId
            return nextId++
        }
    }
}

interface UpdatableAdapter<in Data> {
    fun setDataList(newData: Data)
}

typealias HolderSupplier<T>       = (root: View) -> ListAdapterViewHolder<T>
typealias ItemClickListener<T>    = (position: Int, data: T) -> Unit
typealias UpdateNotifier<T>       = (oldList: List<T>, newList: List<T>, adapter: ListAdapter<T>) -> Unit