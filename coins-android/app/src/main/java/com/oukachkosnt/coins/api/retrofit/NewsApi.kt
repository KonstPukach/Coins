package com.oukachkosnt.coins.api.retrofit

import com.oukachkosnt.coins.data.api.NewsApiResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface NewsApi {
    
    @GET("news/en/{page}")
    fun getNews(@Path("page") page: Int): Single<NewsApiResponse>
}