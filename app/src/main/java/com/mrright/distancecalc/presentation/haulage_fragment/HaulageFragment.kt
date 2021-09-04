package com.mrright.distancecalc.presentation.haulage_fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mrright.distancecalc.databinding.HaulageFragmentBinding
import com.mrright.distancecalc.presentation.haulage_fragment.area.AddAreaFragment
import com.mrright.distancecalc.presentation.haulage_fragment.location.AddLocationFragment
import com.mrright.distancecalc.utils.constants.TabFragment
import com.mrright.distancecalc.utils.helpers.gone
import com.mrright.distancecalc.utils.helpers.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HaulageFragment : Fragment() {

	private var _bind : HaulageFragmentBinding? = null
	private val bind get() = _bind!!

	private val fragments = TabFragment.values().toList()

	private val viewModel : HaulageViewModel by activityViewModels()

	private lateinit var dialogFragment : DialogFragment

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = HaulageFragmentBinding.inflate(inflater, container, false)

		val sectionsPagerAdapter = HaulagePagerAdapter(this@HaulageFragment)
		bind.viewPager.adapter = sectionsPagerAdapter
		TabLayoutMediator(bind.tabs, bind.viewPager) { tab, position ->
			tab.text = resources.getString(fragments[position].id)
		}.attach()

		return bind.root
	}

	@RequiresApi(Build.VERSION_CODES.M)
	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		bind.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
			override fun onPageScrolled(position : Int, positionOffset : Float, positionOffsetPixels : Int) {
				super.onPageScrolled(position, positionOffset, positionOffsetPixels)
				when (position) {
					2, 3, 4 -> bind.fab.gone()
					else -> bind.fab.visible()
				}
			}
		})

		clicks()

	}

	private fun clicks() {
		bind.fab.setOnClickListener {
			when (bind.tabs.selectedTabPosition) {
				0 -> {
					dialogFragment = AddAreaFragment()
				}
				1 -> {
					dialogFragment = AddLocationFragment()
				}
			}
			dialogFragment.show(childFragmentManager, "dialogFragment")
		}
	}


}