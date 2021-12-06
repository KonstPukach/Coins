package com.oukachkosnt.coins.data.api

class NewsApiResponse(val articles: List<NewsItemApiData>, val totalResults: Int)

class Source (
    val name: String
)

class NewsItemApiData(
    val title: String,
    val publishedAt: String,
    val source: Source,
    val url: String,
    val urlToImage: String
)