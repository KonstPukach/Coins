package com.oukachkosnt.coins.api.retrofit

import com.oukachkosnt.coins.data.api.AlertApiData
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface AlertsApi {

    @POST("alerts/create")
    fun createAlert(@Body alert: AlertApiData): Single<AlertApiData>

    @DELETE("alerts/delete/{id}")
    fun deleteAlert(@Path("id") alertId: Long): Completable

    @GET("alerts/user")
    fun getAlerts(@Query("token") userId: String): Single<List<AlertApiData>>
}