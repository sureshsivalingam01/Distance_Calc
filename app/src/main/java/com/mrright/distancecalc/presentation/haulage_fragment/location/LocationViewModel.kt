package com.mrright.distancecalc.presentation.haulage_fragment.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrright.distancecalc.data.Source
import com.mrright.distancecalc.data.firestore.LocationRepository
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
	private val locationRepository : LocationRepository,
) : ViewModel() {

	private val _msgChannel = Channel<MessageEvent>()
	val msg = _msgChannel.receiveAsFlow()

	private var areaText = ""
	private var locationText = ""
	private var haulageRate = 0.0
	private var tollCharge = 0.0
	private var faf = 0.0
	private var gateCharge = 0.0
	private var total = 0.0

	fun setArea(text : String) {
		areaText = text
	}

	fun setLocation(text : String) {
		locationText = text
	}

	fun setHaulageRate(text : String) {
		haulageRate = text.toDouble()
	}

	fun setTollCharge(text : String) {
		tollCharge = text.toDouble()
	}

	fun setFaf(text : String) {
		faf = text.toDouble()
	}

	fun setGateCharge(text : String) {
		gateCharge = text.toDouble()
	}

	fun setTotal(text : String) {
		total = text.toDouble()
	}

	fun addLocation(
		locationName : String, area : String, haulageRate : Double, tollCharge : Double, faf : Double, gateCharge : Double, total : Double
	) {
		viewModelScope.launch(Dispatchers.IO) {
			locationRepository.addLocation(
				locationName, area, haulageRate, tollCharge, faf, gateCharge, total
			).collect {
				when (it) {
					is Source.Failure -> {
						_msgChannel.send(MessageEvent.Toast(it.ex.message ?: ""))
					}
					is Source.Success -> {
						_msgChannel.send(MessageEvent.Toast("Added"))
					}
				}
			}
		}
	}


}