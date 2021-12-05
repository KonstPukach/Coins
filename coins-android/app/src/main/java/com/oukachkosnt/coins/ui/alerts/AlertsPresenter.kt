package com.oukachkosnt.coins.ui.alerts

import com.oukachkosnt.coins.Preferences
import com.oukachkosnt.coins.data.domain.AlertData
import com.oukachkosnt.coins.mvp.list.ListMvpPresenter
import com.oukachkosnt.coins.repository.AlertsRepository

class AlertsPresenter(
    view: AlertsView,
    private val prefs: Preferences
) : ListMvpPresenter<AlertsView>(view) {

    override fun init() {
        addSubscription(
            AlertsRepository
                .getInstance(prefs)
                .getAllAlerts()
                .subscribe(
                    {
                        view?.setData(it)
                        if (it.isEmpty()) view?.showEmptyState()
                    },
                    {
                        view?.showError()
                    }
                )
        )

        addSubscription(
            AlertsRepository
                .getInstance(prefs)
                .subscribeOnAlertsError { view?.showError() }
        )

        addSubscription(
            AlertsRepository
                .getInstance(prefs)
                .subscribeOnRefreshState { view?.setRefreshState(it) }
        )
    }

    fun loadAllAlerts() {
        AlertsRepository
            .getInstance(prefs)
            .loadALlAlerts()
    }

    override fun refreshData() {
        AlertsRepository
            .getInstance(prefs)
            .refreshAllAlerts()
    }

    fun deleteAlert(alert: AlertData) {
        AlertsRepository.getInstance(prefs).removeAlert(alert)
    }
}