package com.mrright.distancecalc.presentation.haulage_fragment.gate_charge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.mrright.distancecalc.databinding.GateChargeFragmentBinding
import com.mrright.distancecalc.presentation.haulage_fragment.HaulageViewModel
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent.Toast
import com.mrright.distancecalc.utils.helpers.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class GateChargeFragment : Fragment() {

	private var _bind : GateChargeFragmentBinding? = null
	private val bind get() = _bind!!

	private val viewModel : GateChargeViewModel by activityViewModels()
	private val haulageViewModel : HaulageViewModel by activityViewModels()

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = GateChargeFragmentBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		collectMsg()
		collectGateCharge()
		collectBtnState()

		clicks()
		edits()

	}

	private fun collectBtnState() {
		lifecycleScope.launchWhenCreated {
			viewModel.isSameValue.collect {
				bind.btnChange.isEnabled = !it
			}
		}
	}

	private fun edits() {
		bind.etGateCharge.addTextChangedListener {
			viewModel.setGateCharge(it.toString())
		}
	}

	private fun collectGateCharge() {
		lifecycleScope.launchWhenCreated {
			viewModel.gateCharge.collect {
				bind.etGateCharge.setText(it.toString())
			}
		}
	}

	private fun clicks() {
		bind.btnChange.setOnClickListener {
			viewModel.changeGateCharge()
		}
	}

	private fun collectMsg() {
		lifecycleScope.launchWhenCreated {
			viewModel.msg.collect {
				when (it) {
					is Toast -> toast(it.msg, it.duration)
					else -> Unit
				}
			}
		}
	}

}