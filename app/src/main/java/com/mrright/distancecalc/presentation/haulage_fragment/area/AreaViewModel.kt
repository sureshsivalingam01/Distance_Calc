package com.mrright.distancecalc.presentation.haulage_fragment.area

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.distancecalc.data.Source
import com.mrright.distancecalc.data.firestore.AreaRepository
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AreaViewModel @Inject constructor(
	private val areaRepository : AreaRepository,
) : ViewModel() {

	private val _msgChannel = Channel<MessageEvent>()
	val msg = _msgChannel.receiveAsFlow()

	fun addArea(areaName : String) {
		viewModelScope.launch(Dispatchers.IO) {
			areaRepository.addArea(areaName).collect {
				when (it) {
					is Source.Failure -> _msgChannel.send(MessageEvent.Toast(it.ex.message ?: ""))
					is Source.Success -> _msgChannel.send(MessageEvent.Toast("Added"))
				}
			}
		}
	}

}