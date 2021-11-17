package com.oukachkosnt.coins.mvp

import androidx.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.io.Serializable

abstract class MvpPresenter<T : MvpView>(view: T?) {
    protected var view: T? = view
        private set

    private val activeSubscriptions = CompositeDisposable()

    abstract fun init()

    @CallSuper
    open fun onViewDestroyed() {
        activeSubscriptions.clear()
        view = null
    }

    fun addSubscription(subscription: Disposable?) {
        subscription?.let { activeSubscriptions.add(it) }
    }

    open fun restoreState(state: Serializable?) {}
    open fun saveState(): Serializable? = null
}