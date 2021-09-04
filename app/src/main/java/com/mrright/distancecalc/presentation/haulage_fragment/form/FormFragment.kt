package com.mrright.distancecalc.presentation.haulage_fragment.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mrright.distancecalc.databinding.FormFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormFragment : Fragment() {


	private var _bind : FormFragmentBinding? = null
	private val bind get() = _bind!!

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = FormFragmentBinding.inflate(inflater, container, false)
		return bind.root
	}


}