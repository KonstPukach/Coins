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
import com.oukachkosnt.coins.data.domain.Currency
import com.oukachkosnt.coins.databinding.DialogFragmentListBinding
import com.oukachkosnt.coins.recycler.ListAdapter
import com.oukachkosnt.coins.recycler.ListAdapterViewHolder
import com.oukachkosnt.coins.recycler.ListAdapterWithStableIds
import com.oukachkosnt.coins.recycler.ListDiffCallback
import com.oukachkosnt.coins.ui.converter.ConverterPresenter
import com.oukachkosnt.coins.utils.addTextChangedListener

class SelectCurrencyDialogFragment : DialogFragment() {
    var presenterProvider: () -> ConverterPresenter? = { null }
    private var role: CurrencyRole? = null

    private val listAdapter = ListAdapterWithStableIds(
        layoutSupplier    = { R.layout.select_list_text_item },
        holderSupplier    = { CurrencyViewHolder(it) },
        itemClickListener = { _, currency -> onItemClicked(currency) },
        getStringId       = { id }
    )

    private lateinit var binding: DialogFragmentListBinding

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ROLE_KEY, role)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            role = savedInstanceState.getSerializable(ROLE_KEY) as? CurrencyRole
        }

        with(binding.recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = listAdapter
        }

        presenterProvider()?.let {
            listAdapter.setDataList(it.getCurrencies())
        }

        binding.searchEditText.addTextChangedListener { filter ->
            presenterProvider()?.let {
                listAdapter.setDataList(
                    it.getCurrencies().filter { it.name.contains(filter, ignoreCase = true) }
                )
            }
        }

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    private fun onItemClicked(clickedCurrency: Currency) {
        if (role == CurrencyRole.BASE) {
            presenterProvider()?.setBaseCurrency(clickedCurrency)
        } else if (role == CurrencyRole.TARGET) {
            presenterProvider()?.setTargetCurrency(clickedCurrency)
        }

        dismiss()
    }

    private class CurrencyViewHolder(root: View) : ListAdapterViewHolder<Currency>(root) {
        private val textItem: TextView = root.findViewById(R.id.text_item)

        override fun bind(data: Currency) {
            textItem.text = data.name
        }
    }

    companion object {
        fun createForBaseCurrency(presenter: ConverterPresenter?) =
            newInstance(presenter, CurrencyRole.BASE)

        fun createForTargetCurrency(presenter: ConverterPresenter?) =
            newInstance(presenter, CurrencyRole.TARGET)

        private fun newInstance(presenter: ConverterPresenter?, role: CurrencyRole) =
            SelectCurrencyDialogFragment().also {
                it.presenterProvider = { presenter }
                it.role = role
            }

        private const val ROLE_KEY = "dialog_currency_role_key"
    }
}

class SelectRealCurrencyDialogFragment : DialogFragment() {
    var callback: ((Currency) -> Unit)? = null
    private var data: List<Currency> = listOf()

    private val listAdapter = ListAdapter(
        layoutSupplier    = { R.layout.select_list_text_item },
        holderSupplier    = { TextItemViewHolder(it) },
        itemClickListener = { _, currency -> onItemClicked(currency) },
        updateNotifier    = ListDiffCallback.getDiffCallbackSupplier({ id == it.id })
    )

    private lateinit var binding: DialogFragmentListBinding

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(DATA_KEY, ArrayList(data))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            data = (savedInstanceState.getSerializable(DATA_KEY) as ArrayList<Currency>).toList()
        }

        listAdapter.setDataList(data)

        with(binding.recyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = listAdapter
        }

        binding.searchEditText.addTextChangedListener { filter ->
            listAdapter.setDataList(data.filter { it.name.contains(filter, ignoreCase = true) })
        }

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    private fun onItemClicked(clickedCurrency: Currency) {
        callback?.invoke(clickedCurrency)
        dismiss()
    }

    private class TextItemViewHolder(root: View) : ListAdapterViewHolder<Currency>(root) {
        private val textItem: TextView = root.findViewById(R.id.text_item)

        override fun bind(data: Currency) {
            textItem.text = data.name
        }
    }

    companion object {
        fun newInstance(data: List<Currency>, callback: (Currency) -> Unit) =
            SelectRealCurrencyDialogFragment().also {
                it.callback = callback
                it.data = data
            }

        private const val DATA_KEY = "currencyDataKey"
    }
}

private enum class CurrencyRole { BASE, TARGET }