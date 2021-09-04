package com.mrright.distancecalc.presentation.haulage_fragment.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrright.distancecalc.databinding.LocationFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationFragment : Fragment() {


	private var _bind : LocationFragmentBinding? = null
	private val bind get() = _bind!!


	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = LocationFragmentBinding.inflate(inflater, container, false)
		return bind.root
	}


}