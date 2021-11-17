package com.oukachkosnt.coins.ui.coins.base

import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.recycler.HolderSupplier
import com.oukachkosnt.coins.recycler.ItemClickListener
import com.oukachkosnt.coins.recycler.ListAdapterWithStableIds

class CoinsAdapter(
    layoutSupplier: () -> Int,
    holderSupplier: HolderSupplier<CryptoCoinData>,
    itemClickListener: ItemClickListener<CryptoCoinData>
) : ListAdapterWithStableIds<CryptoCoinData>(layoutSupplier, holderSupplier, itemClickListener, { id })