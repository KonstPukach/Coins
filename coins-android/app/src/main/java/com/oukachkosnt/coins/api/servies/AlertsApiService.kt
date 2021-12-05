package com.oukachkosnt.coins.api.servies

import android.util.Log
import com.oukachkosnt.coins.api.retrofit.ApiProvider
import com.oukachkosnt.coins.data.domain.AlertData
import com.oukachkosnt.coins.data.mappers.toApiObject
import com.oukachkosnt.coins.data.mappers.toDomainObject
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class AlertsApiService {
    private val api = ApiProvider.alertsApi

    fun createAlert(alert: AlertData): Single<Long>
            = api.createAlert(alert.toApiObject())
                 .subscribeOn(Schedulers.io())
                 .map { it.alert_id!! }
                 .observeOn(AndroidSchedulers.mainThread())

    fun deleteAlert(alert: AlertData): Completable? {
        return alert.id
             ?.let {
                 api.deleteAlert(it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
             }
    }

    fun getAlerts(userId: String): Single<List<AlertData>> {
        return api.getAlerts(userId)
            .subscribeOn(Schedulers.io())
            .map { it.map { it.toDomainObject() } }
            .observeOn(AndroidSchedulers.mainThread())
    }
}