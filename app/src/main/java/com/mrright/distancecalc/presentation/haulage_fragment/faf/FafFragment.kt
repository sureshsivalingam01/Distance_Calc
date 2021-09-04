package com.mrright.distancecalc.presentation.haulage_fragment.faf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.mrright.distancecalc.databinding.FafFragmentBinding
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import com.mrright.distancecalc.utils.helpers.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FafFragment : Fragment() {

	private var _bind : FafFragmentBinding? = null
	private val bind get() = _bind!!

	private val viewModel : FafViewModel by activityViewModels()

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = FafFragmentBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		collectFaf()
		collectMsg()
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
		bind.etFafPercentage.addTextChangedListener {
			viewModel.setGateCharge(it.toString())
		}
	}

	private fun clicks() {
		bind.btnChange.setOnClickListener {
			viewModel.changeFafPercentage()
		}
	}

	private fun collectMsg() {
		lifecycleScope.launchWhenCreated {
			viewModel.msg.collect {
				when (it) {
					is MessageEvent.Toast -> requireContext().toast(it.msg, it.duration)
					else -> Unit
				}
			}
		}
	}

	private fun collectFaf() {
		lifecycleScope.launchWhenCreated {
			viewModel.fafPercentage.collect {
				bind.etFafPercentage.setText(it.toString())
			}
		}
	}


}