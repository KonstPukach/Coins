package com.oukachkosnt.coins.ui.news

import com.oukachkosnt.coins.data.domain.NewsItemData
import com.oukachkosnt.coins.mvp.list.ListMvpView

interface NewsListView : ListMvpView<List<NewsItemData>> {
    fun setPageLoading(isLoading: Boolean)
}