package com.mrright.distancecalc.presentation.haulage_fragment.faf

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
class FafViewModel @Inject constructor(
	private val dbRepository : DbRepository,
) : ViewModel() {

	private val _msgChannel = Channel<MessageEvent>()
	val msg get() = _msgChannel.receiveAsFlow()

	private val _fafPercentage : MutableStateFlow<Double> = MutableStateFlow(0.0)
	val fafPercentage : StateFlow<Double> get() = _fafPercentage

	private val _isSameValue : MutableStateFlow<Boolean> = MutableStateFlow(false)
	val isSameValue : StateFlow<Boolean> get() = _isSameValue

	private var fafText : String = ""

	init {
		viewModelScope.launch(Dispatchers.IO) {
			getFaf()
		}
	}

	fun setGateCharge(text : String) {
		fafText = if (text != "") text else "0.0"
		checkIfSame()
	}

	private fun checkIfSame() {
		_isSameValue.value = _fafPercentage.value.toString() == fafText
	}


	private suspend fun getFaf() {
		dbRepository.dbCallBack().collect {
			when (it) {
				is Resource.Failure -> {
					_fafPercentage.value = 0.0
					fafText = "0.0"
				}
				is Resource.Success -> {
					val value = it.value?.fafPercentage ?: 0.0
					_fafPercentage.value = value
					fafText = value.toString()
					checkIfSame()
				}
			}
		}
	}


	fun changeFafPercentage() {
		viewModelScope.launch(Dispatchers.IO) {
			dbRepository.setFafPercentage(fafText.toDouble()).collect {
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

}













