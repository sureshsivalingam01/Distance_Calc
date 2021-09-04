package com.mrright.distancecalc.presentation.haulage_fragment.area

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.mrright.distancecalc.databinding.FragmentAddAreaBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAreaFragment : DialogFragment() {

	private var _bind : FragmentAddAreaBinding? = null
	private val bind get() = _bind!!

	private val viewModel : AreaViewModel by activityViewModels()

	override fun onCreateView(
		inflater : LayoutInflater, container : ViewGroup?, savedInstanceState : Bundle?
	) : View {
		_bind = FragmentAddAreaBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		clicks()

	}

	private fun clicks() {
		bind.btnAdd.setOnClickListener {
			viewModel.addArea(bind.etArea.text.toString())
			dismiss()
		}
	}


	override fun onStart() {
		super.onStart()
		dialog?.window?.setLayout(
			WindowManager.LayoutParams.MATCH_PARENT,
			WindowManager.LayoutParams.WRAP_CONTENT,
		)
	}

}