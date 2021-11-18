package com.oukachkosnt.coins.ui.coins.details

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.navigation.navArgs
import androidx.viewpager.widget.ViewPager
import com.oukachkosnt.coins.R
import com.oukachkosnt.coins.data.domain.CryptoCoinData
import com.oukachkosnt.coins.databinding.ActivityCoinDetailsBinding
import com.oukachkosnt.coins.mvp.MvpActivity
import com.oukachkosnt.coins.mvp.MvpView
import com.oukachkosnt.coins.ui.coins.details.coin.CoinDetailsFragment
import com.oukachkosnt.coins.ui.coins.pager.Page
import com.oukachkosnt.coins.ui.coins.pager.ViewPagerAdapter

class CoinDetailsPagesActivity : MvpActivity<CoinDetailsPagesPresenter>(R.layout.activity_coin_details),
    CoinDetailsPagesView, CoinIdProvider {

    private var isFavoriteCoin = false
    private lateinit var binding: ActivityCoinDetailsBinding
    private val args: CoinDetailsPagesActivityArgs by navArgs()

    override fun createPresenter() = CoinDetailsPagesPresenter(this, getCoinData().id, null)

    override fun bindView() {
        binding = ActivityCoinDetailsBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)

        val pages = listOf(
                Page(getString(R.string.details_tab_title)) { CoinDetailsFragment() },
                Page(getString(R.string.details_tab_title)) { CoinDetailsFragment() },
//                Page(getString(R.string.chart_tab_title)) { CoinPriceChartFragment() }
        )

        binding.viewPager.adapter = ViewPagerAdapter(pages, supportFragmentManager)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    binding.fab.show()
                } else {
                    binding.fab.hide()
                }
            }
        })

        binding.tabs.isVisible = true
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    override fun setData(coin: CryptoCoinData) {
        binding.toolbar.title = coin.name
        isFavoriteCoin = coin.isFavorite
        invalidateOptionsMenu()
    }

    override fun showError() {
        showSnack(binding.viewPager, R.string.generic_error_message)
    }

    override fun showFirstFavoriteHelpMessage() {
        showSnack(binding.viewPager, R.string.first_time_favorite_message)
    }

    override fun showFirstUnfavoriteHelpMessage() {
        showSnack(binding.viewPager, R.string.first_time_unfavorite_message)
    }

    override fun getCoinData(): CryptoCoinData = args.coinData

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_coin_details, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_favorite)
            .setIcon(if (isFavoriteCoin) R.drawable.ic_star_white_48dp
                     else                R.drawable.ic_star_outline_white_48dp)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_favorite) {
            presenter?.switchFavoriteStatus()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val COIN_DATA_KEY = "coinData"

        fun makeArgs(coin: CryptoCoinData) = Bundle().also { it.putSerializable(COIN_DATA_KEY, coin) }
    }
}

interface CoinDetailsPagesView : MvpView {
    fun setData(coin: CryptoCoinData)
    fun showError()
    fun showFirstFavoriteHelpMessage()
    fun showFirstUnfavoriteHelpMessage()
}

interface CoinIdProvider {
    fun getCoinData(): CryptoCoinData
}
