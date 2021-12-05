package com.oukachkosnt.coins.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.databinding.DialogFragmentListBinding
import com.oukachkosnt.coins.recycler.ListAdapterViewHolder
import com.oukachkosnt.coins.recycler.ListAdapterWithStableIds
import com.oukachkosnt.coins.ui.alerts.create.CreateAlertPresenter
import com.oukachkosnt.coins.utils.addTextChangedListener

class SelectCoinDialogFragment : DialogFragment() {
    var presenter: CreateAlertPresenter? = null

    private lateinit var binding: DialogFragmentListBinding

    private val listAdapter =
        ListAdapterWithStableIds(
            { R.layout.select_list_text_item },
            { CoinViewHolder(it) },
            { _, currency -> onItemClicked(currency) },
            { id }
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding.recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = listAdapter
        }

        presenter?.let {
            listAdapter.setDataList(it.getCoins())
        }

        binding.searchEditText.addTextChangedListener { filter ->
            presenter?.let {
                listAdapter.setDataList(
                    it.getCoins().filter { it.name.contains(filter, ignoreCase = true) }
                )
            }
        }
    }

    private fun onItemClicked(coin: CryptoCoinData) {
        presenter?.setCurrentCoin(coin)
        dismiss()
    }

    private class CoinViewHolder(root: View) : ListAdapterViewHolder<CryptoCoinData>(root) {
        private val textItem: TextView = itemView.findViewById(R.id.text_item)

        override fun bind(data: CryptoCoinData) {
            textItem.text = data.name
        }
    }

    companion object {
        fun newInstance(presenter: CreateAlertPresenter) = SelectCoinDialogFragment().also {
            it.presenter = presenter
        }
    }
}