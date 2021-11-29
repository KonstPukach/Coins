package com.oukachkosnt.coins.mvp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

abstract class MvpActivity<T : MvpPresenter<out MvpView>>(@LayoutRes private val layoutResId: Int) : AppCompatActivity() {
    companion object {
        private const val PRESENTER_STATE_KEY = "presenter_state"
    }

    protected var presenter: T? = null
        private set

    protected abstract fun createPresenter(): T
    protected abstract fun bindView()

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView()

        presenter = createPresenter()
        presenter?.restoreState(savedInstanceState?.getSerializable(PRESENTER_STATE_KEY))
        presenter?.init()

        onPresenterInitialized()
    }

    open fun onPresenterInitialized() { }

    override fun onDestroy() {
        presenter?.onViewDestroyed()

        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(PRESENTER_STATE_KEY, presenter?.saveState())
    }

    protected fun showToast(@StringRes messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()
    }

    protected fun showSnack(view: View, @StringRes messageResId: Int) {
        view.post {
            Snackbar.make(view, messageResId, Snackbar.LENGTH_LONG).show()
        }
    }
}