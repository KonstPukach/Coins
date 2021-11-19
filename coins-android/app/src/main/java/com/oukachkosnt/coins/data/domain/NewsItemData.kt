package com.oukachkosnt.coins.data.domain

data class NewsItemData(
    val id: Int,
    val title: String,
    val postedAt: String,
    val source: String,
    val sourceUrl: String,
    val imageUrl: String,
    val viewsCount: Int
)