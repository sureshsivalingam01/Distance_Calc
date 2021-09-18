package com.mrright.distancecalc.presentation.haulage_fragment.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.mrright.distancecalc.databinding.FragmentAddLocationBinding
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import com.mrright.distancecalc.utils.helpers.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddLocationFragment : DialogFragment() {

	private var _bind : FragmentAddLocationBinding? = null
	private val bind get() = _bind!!

	private val viewModel : LocationViewModel by activityViewModels()

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = FragmentAddLocationBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		collectMsg()

		edits()
		clicks()

		val s = ".0".toDouble()

		bind.etTotal.setText(s.toString())

	}

	private fun edits() {
		with(bind) {
			etSelectArea.addTextChangedListener {
				viewModel.setArea(it.toString())
			}
			etLocationName.addTextChangedListener {
				viewModel.setLocation(it.toString())
			}
			etHaulageRage.addTextChangedListener {
				try {
					viewModel.setHaulageRate(it.toString())
				} catch (e : Exception) {
					viewModel.setHaulageRate("0")
				}
			}
			etTollCharge.addTextChangedListener {
				try {
					viewModel.setTollCharge(it.toString())
				} catch (e : Exception) {
					viewModel.setTollCharge("0")
				}
			}
			etFaf.addTextChangedListener {
				try {
					viewModel.setFaf(it.toString())
				} catch (e : Exception) {
					viewModel.setFaf("0")
				}
			}
			etGateCharge.addTextChangedListener {
				try {
					viewModel.setGateCharge(it.toString())
				} catch (e : Exception) {
					viewModel.setGateCharge("0")
				}
			}
			etTotal.addTextChangedListener {
				try {
					viewModel.setTotal(it.toString())
				} catch (e : Exception) {
					viewModel.setTotal("0")
				}
			}
		}
	}

	private fun clicks() {
		bind.btnAdd.setOnClickListener {

			toast("TODO")
			/*viewModel.addLocation(
				locationName = bind.etLocationName.text.toString(),
				area = bind.etSelectArea.text.toString(),
				haulageRate = bind.etHaulageRage.text.toString().toDouble(),
				tollCharge = bind.etTollCharge.text.toString().toDouble(),
				faf = bind.etFaf.text.toString().toDouble(),
				gateCharge = bind.etGateCharge.text.toString().toDouble(),
				total = bind.etTotal.text.toString().toDouble(),
			)
			dismiss()*/
		}
	}

	private fun collectMsg() {
		lifecycleScope.launchWhenCreated {
			viewModel.msg.collect {
				when (it) {
					is MessageEvent.SnackBar -> Unit
					is MessageEvent.Toast -> toast(it.msg, it.duration)
					is MessageEvent.ToastStringRes -> Unit
				}
			}
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