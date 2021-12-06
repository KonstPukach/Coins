package com.oukachkosnt.coins.repository

import com.oukachkosnt.coins.api.servies.NewsApiService
import com.oukachkosnt.coins.data.domain.NewsItemData
import io.reactivex.Single


object NewsRepository {
    private val newsApi = NewsApiService()

    val newsPagedLoader = PageLoader({ newsApi.getNewsPage(it) })
}