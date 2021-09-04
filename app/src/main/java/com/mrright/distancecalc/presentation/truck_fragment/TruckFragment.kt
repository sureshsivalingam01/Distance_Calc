package com.mrright.distancecalc.presentation.truck_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrright.distancecalc.databinding.TruckFragmentBinding
import com.mrright.distancecalc.presentation.adapters.TrucksFireStoreAdapter
import com.mrright.distancecalc.utils.constants.View.NEW
import com.mrright.distancecalc.utils.constants.View.UPDATE
import com.mrright.distancecalc.utils.helpers.openActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TruckFragment : Fragment() {

	private var _bind : TruckFragmentBinding? = null
	private val bind get() = _bind!!

	private lateinit var adapter : TrucksFireStoreAdapter

	private val viewModel : TruckViewModel by activityViewModels()


	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = TruckFragmentBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(
		view : View,
		savedInstanceState : Bundle?,
	) {
		super.onViewCreated(view, savedInstanceState)

		adapter = TrucksFireStoreAdapter(viewModel.options) {
			requireActivity().openActivity(AddTruckActivity::class.java) {
				putSerializable("view", UPDATE)
				putString("truckId", it)
			}
		}

		adapter.startListening()

		bind.rvTrucks.also {
			it.adapter = adapter
			it.layoutManager = LinearLayoutManager(requireContext())
		}

		click()

	}

	private fun click() {
		bind.fabAdd.setOnClickListener {
			requireActivity().openActivity(AddTruckActivity::class.java) {
				putSerializable("view", NEW)
			}
		}
	}


	override fun onDestroy() {
		super.onDestroy()
		adapter.stopListening()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_bind = null
	}


}