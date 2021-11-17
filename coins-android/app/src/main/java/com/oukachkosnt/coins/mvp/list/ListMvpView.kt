package com.oukachkosnt.coins.mvp.list

import com.oukachkosnt.coins.mvp.MvpView

interface ListMvpView<in DataType> : MvpView {
    fun setData(newData: DataType)
    fun showError()
    fun showEmptyState()
    fun setRefreshState(isRefreshing: Boolean)
}