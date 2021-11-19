package com.oukachkosnt.coins.data.api

class NewsApiResponse(val news: List<NewsItemApiData>)

class NewsItemApiData(
    val id: Int,
    val name: String,
    val datetime: String,
    val source: String,
    val link: String,
    val image: String,
    val views: Int
)