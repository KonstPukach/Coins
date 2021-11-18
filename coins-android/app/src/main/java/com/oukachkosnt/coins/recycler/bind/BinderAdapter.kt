package com.oukachkosnt.coins.recycler.bind

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.oukachkosnt.coins.recycler.HolderSupplier
import com.oukachkosnt.coins.recycler.ListAdapterViewHolder
import com.oukachkosnt.coins.recycler.UpdatableAdapter

open class BinderAdapter<in T>(private val mapper: (T) -> List<Binder<*, *>>,
                               private var bindersList: List<Binder<*, *>> = emptyList())
    : RecyclerView.Adapter<ListAdapterViewHolder<*>>(), UpdatableAdapter<T> {

    private val viewTypeManager = ViewTypeManager()

    override fun setDataList(newData: T) {
        bindersList = mapper(newData)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) = viewTypeManager.getViewType(bindersList[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapterViewHolder<*> {
        val itemView = LayoutInflater.from(parent.context)
                                     .inflate(viewTypeManager.getLayoutResId(viewType), parent, false)

        return viewTypeManager.getViewHolderSupplier(viewType)(itemView)
    }

    override fun onBindViewHolder(holder: ListAdapterViewHolder<*>, position: Int) {
        bindersList[position].bind(holder)
    }

    override fun getItemCount() = bindersList.size

    private class ViewTypeManager {
        private var nextType = 1
        private val knownTypes = mutableMapOf<LayoutResType, LayoutType>()
        private val knownLayouts = mutableMapOf<LayoutType, LayoutResType>()
        private val knownViewHolderSuppliers = mutableMapOf<LayoutType, HolderSupplier<*>>()

        fun getViewType(binder: Binder<*, *>): Int {
            if (binder.layoutResId !in knownTypes) {
                knownTypes[binder.layoutResId] = nextType
                knownViewHolderSuppliers[nextType] = binder.viewHolderSupplier
                knownLayouts[nextType] = binder.layoutResId
                nextType++
            }

            return knownTypes.getValue(binder.layoutResId)
        }

        fun getLayoutResId(viewType: Int) = knownLayouts.getValue(viewType)

        fun getViewHolderSupplier(viewType: Int) = knownViewHolderSuppliers.getValue(viewType)
    }
}

open class Binder<T, in VH: ListAdapterViewHolder<T>>(@LayoutRes val layoutResId: Int,
                                                      val viewHolderSupplier: HolderSupplier<T>,
                                                      val data: T,
                                                      private val itemClickListener: ((T) -> Unit)? = null) {
    fun bind(holder: ListAdapterViewHolder<*>) {
        @Suppress("UNCHECKED_CAST")
        (holder as? VH)?.let {
            it.bind(data)
            it.setItemClickListener { itemClickListener?.let { it(data) } }
        }
    }
}

private typealias LayoutType = Int
private typealias LayoutResType = Int