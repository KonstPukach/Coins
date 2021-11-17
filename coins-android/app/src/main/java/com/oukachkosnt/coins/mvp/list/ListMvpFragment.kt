package com.oukachkosnt.coins.mvp.list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.databinding.RefreshableRecyclerBinding
import com.oukachkosnt.coins.mvp.MvpFragment
import com.oukachkosnt.coins.recycler.UpdatableAdapter


abstract class ListMvpFragment<in Data, Adapter, Presenter: ListMvpPresenter<out ListMvpView<Data>>>(
    private val useSwipeRefresh: Boolean = true
) : MvpFragment<Presenter>(R.layout.refreshable_recycler), ListMvpView<Data>
      where Adapter: UpdatableAdapter<Data>, Adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>{

    protected lateinit var listAdapter: Adapter
        private set

    abstract fun createAdapter(): Adapter
    abstract fun initRecycler(recycler: RecyclerView)

    protected lateinit var binding: RefreshableRecyclerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    final override fun bindView(rootView: View) {
        binding = RefreshableRecyclerBinding.bind(rootView)
        listAdapter = createAdapter()
        with(binding.recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)

            initRecycler(this)

            adapter = listAdapter
        }

        binding.swipeRefresh.setOnRefreshListener { presenter?.refreshData() }

        setListViewState(ViewState.SHOW_LOADING)
    }

    override fun setData(newData: Data) {
        listAdapter.setDataList(newData)
        setListViewState(ViewState.SHOW_CONTENT)

        onNewDataSet(newData)
    }

    open fun onNewDataSet(newData: Data) { }

    override fun setRefreshState(isRefreshing: Boolean) {
        binding.swipeRefresh.isRefreshing = isRefreshing
    }

    override fun showError() {
        if (listAdapter.itemCount == 0) {
            setListViewState(ViewState.SHOW_ERROR)
        } else {
            showSnack(R.string.generic_error_message)
        }
    }

    override fun showEmptyState() {
       setListViewState(ViewState.SHOW_EMPTY)
    }

    private fun setListViewState(state: ViewState) {
        fun visibleIf(visibleState: ViewState) = state == visibleState
        with(binding) {
            errorView.isVisible      = visibleIf(ViewState.SHOW_ERROR)
            loadingView.isVisible    = visibleIf(ViewState.SHOW_LOADING)
            recyclerView.isVisible   = visibleIf(ViewState.SHOW_CONTENT)
            emptyStateView.isVisible = visibleIf(ViewState.SHOW_EMPTY)
        }

        enableSwipeRefreshIfLoadFinished()
    }

    private fun enableSwipeRefreshIfLoadFinished() {
        binding.swipeRefresh.isEnabled = useSwipeRefresh && binding.loadingView.visibility == View.INVISIBLE
    }
}