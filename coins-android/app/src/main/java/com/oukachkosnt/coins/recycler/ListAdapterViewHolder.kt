package com.oukachkosnt.coins.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ListAdapterViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(data: T)

    fun setItemClickListener(listener: () -> Unit) {
        itemView.setOnClickListener { listener() }
    }
}
