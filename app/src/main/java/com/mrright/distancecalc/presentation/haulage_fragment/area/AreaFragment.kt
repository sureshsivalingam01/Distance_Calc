package com.mrright.distancecalc.presentation.haulage_fragment.area

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.mrright.distancecalc.databinding.AreaFragmentBinding
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent.*
import com.mrright.distancecalc.utils.helpers.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AreaFragment : Fragment() {

	private var _bind : AreaFragmentBinding? = null
	private val bind get() = _bind!!

	private val viewModel : AreaViewModel by activityViewModels()

	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View {
		_bind = AreaFragmentBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		collectMsg()

	}

	private fun collectMsg() {
		lifecycleScope.launchWhenCreated {
			viewModel.msg.collect {
				when (it) {
                    is SnackBar -> Unit
                    is Toast -> toast(it.msg, it.duration)
                    is ToastStringRes -> Unit
                }
			}
		}
	}


}