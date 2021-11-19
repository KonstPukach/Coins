package com.oukachkosnt.coins.repository

import com.oukachkosnt.coins.api.servies.NewsApiService
import com.oukachkosnt.coins.data.domain.NewsItemData
import io.reactivex.Single


object NewsRepository {
    private val newsApi = NewsApiService()

    val stub = NewsItemData(
        id = 1,
        title = "Title",
        postedAt = "20.12.2021",
        source = "Source",
        sourceUrl = "https://krasnodar.bezformata.com/listnews/krasnodara-razrabotayut-programmi/99618136/",
        imageUrl = "https://dumskaya.net/pics/b7/picturepicture_163704819715578068372530_88677.jpg",
        viewsCount = 1,
    )

    val newsPagedLoader = PageLoader({ newsApi.getNewsPage(it) })
}