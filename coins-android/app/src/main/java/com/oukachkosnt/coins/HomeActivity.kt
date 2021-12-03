package com.oukachkosnt.coins

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.oukachkosnt.coins.databinding.ActivityMainBinding

class HomeActivity : AppCompatActivity(),
    FloatingActionButtonProvider,
    CollapsingToolbarOwner {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.isVisible = false
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_coins,
                R.id.nav_news,
                R.id.nav_market_stats,
                R.id.nav_converter
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TOOLBAR_TILE_KEY, supportActionBar?.title?.toString())
    }

    companion object {
        private const val TOOLBAR_TILE_KEY = "toolbar_title"
    }

    override fun getActionButton(): FloatingActionButton = binding.appBarMain.fab

    override fun enableCollapsingToolbar(isEnabled: Boolean) {
        with (binding.appBarMain.toolbar.layoutParams as AppBarLayout.LayoutParams) {
            scrollFlags = if (isEnabled) SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS else 0
        }
    }
}

interface FloatingActionButtonProvider {
    fun getActionButton(): FloatingActionButton
}

interface CollapsingToolbarOwner {
    fun enableCollapsingToolbar(isEnabled: Boolean)
}