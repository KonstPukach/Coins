package com.oukachkosnt.coins.ui.coins.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class ViewPagerAdapter(private val pages: List<Page>,
                       fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int) = pages[position].fragmentSupplier()

    override fun getCount() = pages.size

    override fun getPageTitle(position: Int) = pages[position].name
}

class Page(val name: String, val fragmentSupplier: () -> Fragment)