package com.oukachkosnt.coins.data.mappers

import com.oukachkosnt.coins.data.api.NewsItemApiData
import com.oukachkosnt.coins.data.domain.NewsItemData

fun NewsItemApiData.toDomainData() = NewsItemData(
    id         = id,
    title      = name,
    postedAt   = datetime,
    source     = source,
    sourceUrl  = link,
    imageUrl   = image,
    viewsCount = views
)