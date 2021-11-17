package com.oukachkosnt.coins.mvp.list

import com.oukachkosnt.coins.mvp.MvpPresenter

abstract class ListMvpPresenter<ViewType: ListMvpView<*>>(view: ViewType) : MvpPresenter<ViewType>(view) {
    open fun refreshData() { }
}