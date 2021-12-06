package com.oukachkosnt.coins.ui.alerts

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.oukachkosnt.coins.Preferences
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.AlertData
import com.oukachkosnt.coins.databinding.AlertListItemBinding
import com.oukachkosnt.coins.formatters.formatPriceUsdAutoprecision
import com.oukachkosnt.coins.mvp.list.ListMvpFragment
import com.oukachkosnt.coins.recycler.ListAdapter
import com.oukachkosnt.coins.recycler.ListAdapterViewHolder
import com.oukachkosnt.coins.recycler.ListDiffCallback

class AlertsFragment :
    ListMvpFragment<List<AlertData>, ListAdapter<AlertData>, AlertsPresenter>(useSwipeRefresh = true),
    AlertsView {
 
    override fun createPresenter() = AlertsPresenter(this, Preferences(requireContext()))

    override fun createAdapter() = ListAdapter(
        { R.layout.alert_list_item },
        { AlertItemViewHolder(it) },
        updateNotifier = ListDiffCallback.getDiffCallbackSupplier({ id == it.id })
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.invalidateOptionsMenu()
    }

    override fun onResume() {
        super.onResume()
        presenter?.loadAllAlerts()
    }

    override fun initRecycler(recycler: RecyclerView) {
        resources.getDimensionPixelSize(R.dimen.margin_small).let {
            recycler.setPadding(0, it, 0, it)
        }
    }

    override fun onNewDataSet(newData: List<AlertData>) {
        activity?.invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.alerts_list_menu, menu)
        menu.findItem(R.id.action_add_alert).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add_alert -> {
            findNavController().navigate(AlertsFragmentDirections.actionNavAlertsToNavCreateAlert())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private inner class AlertItemViewHolder(root: View) : ListAdapterViewHolder<AlertData>(root) {
        private val binding = AlertListItemBinding.bind(itemView)

        override fun bind(data: AlertData) {
            with(binding) {
                alertCoinName.text = data.name
                alertLowLimit.text = getString(
                    R.string.alert_low_limit_format,
                    data.lowLimit.formatPriceUsdAutoprecision()
                )
                alertHighLimit.text = getString(
                    R.string.alert_high_limit_format,
                    data.highLimit.formatPriceUsdAutoprecision()
                )

                alertDelete.setOnClickListener { presenter?.deleteAlert(data) }
            }
        }
    }
}
