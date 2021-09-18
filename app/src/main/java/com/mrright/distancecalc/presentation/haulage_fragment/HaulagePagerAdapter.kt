package com.mrright.distancecalc.presentation.haulage_fragment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mrright.distancecalc.utils.constants.HaulageTabs

class HaulagePagerAdapter(
	fragment : Fragment,
) : FragmentStateAdapter(fragment) {

    private val fragments = HaulageTabs.values().toList()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position].fragment

}