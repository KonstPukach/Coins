package com.oukachkosnt.coins.recycler

import androidx.recyclerview.widget.DiffUtil

class ListDiffCallback<T>(
    private val oldList: List<T>,
    private val newList: List<T>,
    private val compareId: T.(T) -> Boolean,
    private val compare: T.(T) -> Boolean
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].compareId(newList[newItemPosition])

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].compare(newList[newItemPosition])

    companion object {
        fun <T> getDiffCallbackSupplier(
            compareId: T.(T) -> Boolean,
            compare: T.(T) -> Boolean = { this == it },
            detectMoves: Boolean = false
        ): UpdateNotifier<T> {
            return { old, new, adapter ->
                DiffUtil.calculateDiff(ListDiffCallback(old, new, compareId, compare), detectMoves)
                    .dispatchUpdatesTo(adapter)
            }
        }
    }
}