package com.oukachkosnt.coins.mvp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

abstract class MvpFragment<T : MvpPresenter<out MvpView>>(@LayoutRes private val layoutResId: Int) : Fragment() {
    companion object {
        private const val PRESENTER_STATE_KEY = "presenter_state"
    }

    protected var presenter: T? = null
        private set

    protected abstract fun createPresenter(): T
    protected abstract fun bindView(rootView: View)

    final override fun onCreateView(inflater: LayoutInflater,
                                    container: ViewGroup?,
                                    savedInstanceState: Bundle?): View
            = inflater.inflate(layoutResId, container, false)

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindView(view)
        presenter = createPresenter()
        presenter?.restoreState(savedInstanceState?.getSerializable(PRESENTER_STATE_KEY))
        presenter?.init()
    }

    @CallSuper
    override fun onDestroyView() {
        presenter?.onViewDestroyed()

        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(PRESENTER_STATE_KEY, presenter?.saveState())
    }

    protected fun showToast(@StringRes messageResId: Int) {
        Toast.makeText(activity, messageResId, Toast.LENGTH_LONG).show()
    }

    protected fun showSnack(@StringRes messageResId: Int) {
        view?.let {
            it.post {
                Snackbar.make(it, messageResId, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}