package com.mrright.distancecalc.presentation.haulage_fragment.gate_charge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.distancecalc.data.Resource
import com.mrright.distancecalc.data.Source
import com.mrright.distancecalc.data.firestore.DbRepository
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GateChargeViewModel @Inject constructor(
	private val dbRepository : DbRepository,
) : ViewModel() {

	private val _msgChannel = Channel<MessageEvent>()
	val msg get() = _msgChannel.receiveAsFlow()

	private val _isSameValue : MutableStateFlow<Boolean> = MutableStateFlow(false)
	val isSameValue : StateFlow<Boolean> get() = _isSameValue

	private val _gateCharge : MutableStateFlow<Double> = MutableStateFlow(0.0)
	val gateCharge : StateFlow<Double> get() = _gateCharge

	private var gateChargeText : String = ""


	init {
		viewModelScope.launch(Dispatchers.IO) {
			getGateCharge()
		}
	}

	fun setGateCharge(text : String) {
		gateChargeText = if (text != "") text else "0.0"
		checkIfSame()
	}

	private fun checkIfSame() {
		_isSameValue.value = _gateCharge.value.toString() == gateChargeText
	}


	fun changeGateCharge() {
		viewModelScope.launch(Dispatchers.IO) {
			dbRepository.setGateCharge(gateChargeText.toDouble()).collect {
				when (it) {
					is Source.Failure -> {
						_msgChannel.send(MessageEvent.Toast(it.ex.message ?: ""))
					}
					is Source.Success -> {
						_msgChannel.send(MessageEvent.Toast("Changed"))
					}
				}
			}
		}
	}


	private suspend fun getGateCharge() {
		dbRepository.dbCallBack().collect {
			when (it) {
				is Resource.Failure -> {
					gateChargeText = "0.0"
					_gateCharge.value = 0.0
				}
				is Resource.Success -> {
					val value = it.value?.gateCharge ?: 0.0
					gateChargeText = value.toString()
					_gateCharge.value = value
					checkIfSame()
				}
			}
		}
	}


}



















