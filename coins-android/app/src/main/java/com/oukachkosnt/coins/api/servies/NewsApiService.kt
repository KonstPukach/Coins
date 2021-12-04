package com.oukachkosnt.coins.api.servies

import com.oukachkosnt.coins.api.retrofit.ApiProvider
import com.oukachkosnt.coins.data.domain.NewsItemData
import com.oukachkosnt.coins.data.mappers.toDomainData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NewsApiService {

    fun getNewsPage(page: Int): Single<List<NewsItemData>> {
        if (page < 1) throw IllegalArgumentException("Should be non-negative")

        return ApiProvider
            .newsApi
            .getNews(page)
            .subscribeOn(Schedulers.io())
            .map {
                it.articles.map { data ->
                    data.toDomainData()
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
    }
}