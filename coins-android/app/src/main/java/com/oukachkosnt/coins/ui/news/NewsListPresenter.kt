package com.oukachkosnt.coins.ui.news

import com.oukachkosnt.coins.mvp.list.ListMvpPresenter
import com.oukachkosnt.coins.repository.NewsRepository

class NewsListPresenter(view: NewsListView) : ListMvpPresenter<NewsListView>(view) {
    override fun init() {
        NewsRepository.newsPagedLoader
            .also {
                addSubscription(it.subscribeOnData { view?.setData(it) })
                addSubscription(it.subscribeOnLoadingState { view?.setPageLoading(it) })
                addSubscription(it.subscribeOnError { view?.showError() })
                addSubscription(it.subscribeOnResetState { view?.setRefreshState(it) })
            }
    }

    override fun refreshData() {
        NewsRepository.newsPagedLoader.reset()
    }

    fun requestNextNewsPage() {
        NewsRepository.newsPagedLoader.loadNextPage()
    }
}