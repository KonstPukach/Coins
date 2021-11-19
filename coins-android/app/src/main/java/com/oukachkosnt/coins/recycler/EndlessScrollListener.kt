package com.oukachkosnt.coins.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EndlessScrollListener(
    private val loadingRequest: () -> Unit,
    private val scrollThreshold: Int = 1
) : RecyclerView.OnScrollListener() {

    private var canTriggerLoad = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager

        with(layoutManager) {
            if (findLastVisibleItemPosition() + scrollThreshold >= itemCount - 1) {
                if (canTriggerLoad) {
                    canTriggerLoad = false
                    loadingRequest()
                }
            } else {
                canTriggerLoad = true
            }
        }
    }
}