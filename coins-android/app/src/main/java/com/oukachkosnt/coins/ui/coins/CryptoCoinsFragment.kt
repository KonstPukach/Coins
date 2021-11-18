package com.oukachkosnt.coins.ui.coins

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.oukachkosnt.coins.ui.coins.favorite.FavoriteCryptoCoinsFragment
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.TabLayoutProvider
import com.oukachkosnt.coins.databinding.FragmentViewPagerBinding
import com.oukachkosnt.coins.ui.coins.all.AllCryptoCoinsListFragment
import com.oukachkosnt.coins.ui.coins.pager.Page
import com.oukachkosnt.coins.ui.coins.pager.ViewPagerAdapter
import com.oukachkosnt.coins.ui.coins.top.TopCryptoCoinsFragment

class CryptoCoinsFragment : Fragment() {
    private var searchView: SearchView? = null
    private val menuItems = mutableListOf<MenuItem>()

    private lateinit var binding: FragmentViewPagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentViewPagerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pages = listOf(
            Page(getString(R.string.all_coins_page_title)) { AllCryptoCoinsListFragment() },
            Page(getString(R.string.favorite_coins_page_title)) { FavoriteCryptoCoinsFragment() },
            Page(getString(R.string.top_10_coins_page_title)) { TopCryptoCoinsFragment() }
        )

        with(binding.viewPager) {
            adapter = ViewPagerAdapter(pages, childFragmentManager)
            addOnPageChangeListener(
                object : ViewPager.SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        updateMenuVisibility(position)
                    }
                }
            )
        }
        binding.viewPager.currentItem = 1
    }

    override fun onResume() {
        super.onResume()

        (activity as? TabLayoutProvider)
            ?.getTabLayout()
            ?.let {
                it.visibility = View.VISIBLE
                it.setupWithViewPager(binding.viewPager)
            }
    }

    private fun updateMenuVisibility(pagerPosition: Int) {
        with(pagerPosition != TOP_COINS_INDEX) {
            menuItems.forEach { it.isVisible = this }
            searchView?.visibility = if (this) View.VISIBLE else View.INVISIBLE
        }
    }

    companion object {
        private const val SORT_ORDER_DIALOG = "sortOrderDialogTag"
        private const val TOP_COINS_INDEX = 2
    }
}