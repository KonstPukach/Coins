package com.oukachkosnt.coins.data.mappers

import com.oukachkosnt.coins.data.api.NewsItemApiData
import com.oukachkosnt.coins.data.domain.NewsItemData

fun NewsItemApiData.toDomainData() = NewsItemData(
    title      = title,
    postedAt   = publishedAt,
    source     = source.name,
    sourceUrl  = url,
    imageUrl   = urlToImage
)