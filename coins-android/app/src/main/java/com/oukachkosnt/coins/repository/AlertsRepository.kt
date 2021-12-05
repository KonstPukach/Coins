package com.oukachkosnt.coins.repository

import android.annotation.SuppressLint
import android.util.Log
import com.oukachkosnt.coins.Preferences
import com.oukachkosnt.coins.api.servies.AlertsApiService
import com.oukachkosnt.coins.data.domain.AlertData
import com.oukachkosnt.coins.utils.subscribeOnAsError
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

object AlertsRepository {
    private var instance: AlertsRepositoryObject? = null

    fun getInstance(prefs: Preferences): AlertsRepositoryObject {
        if (instance == null) instance = AlertsRepositoryObject(prefs)
        return instance!!
    }
}

@SuppressLint("CheckResult")
class AlertsRepositoryObject(private val prefs: Preferences) {
    private val api       = AlertsApiService()
    private val alerts    = BehaviorSubject.create<List<AlertData>>()
    private val isError   = BehaviorSubject.createDefault(false)
    private val isRefresh = BehaviorSubject.createDefault(false)

    fun loadALlAlerts() {
        prefs.firebaseToken?.let {
            api.getAlerts(it)
                .subscribe(
                    { alerts.onNext(it) },
                    { isError.onNext(true) }
                )
        } ?: isError.onNext(true)
    }

    fun createAlert(alert: AlertData) {
        api.createAlert(alert).subscribe(
            { Log.d("Alerts", "Alert id = $it has been created")},
            { Log.d("Alerts", "Alert has not been created. Error") }
        )
    }

    fun removeAlert(alert: AlertData) {
        api.deleteAlert(alert)?.subscribe(
            { refreshAllAlerts() },
            { }
        )
    }

    fun refreshAllAlerts() {
        isRefresh.onNext(true)
        loadAllAlerts()
    }

    private fun loadAllAlerts() {
        prefs.firebaseToken?.let {
            api.getAlerts(it)
                .subscribe(
                    {
                        alerts.onNext(it)
                        isError.onNext(false)
                        isRefresh.onNext(false)
                    },
                    {
                        isError.onNext(true)
                    }
                )
        } ?: isError.onNext(true)
    }

    fun getAllAlerts(): Flowable<List<AlertData>> = alerts.toFlowable(BackpressureStrategy.LATEST)

    fun subscribeOnAlertsError(onError: () -> Unit): Disposable = isError.subscribeOnAsError(onError)

    fun subscribeOnRefreshState(consumer: (Boolean) -> Unit): Disposable = isRefresh.subscribe(consumer)
}