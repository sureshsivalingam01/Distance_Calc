package com.mrright.distancecalc.presentation.truck_fragment

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.mrright.distancecalc.data.Resource
import com.mrright.distancecalc.data.Source
import com.mrright.distancecalc.data.firestore.TruckRepository
import com.mrright.distancecalc.data.firestore.models.TruckDTO
import com.mrright.distancecalc.data.firestore.models.toLocations
import com.mrright.distancecalc.data.firestore.models.toRanges
import com.mrright.distancecalc.di.Database
import com.mrright.distancecalc.models.*
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import com.mrright.distancecalc.utils.constants.Collection
import com.mrright.distancecalc.utils.constants.View
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TruckViewModel @Inject constructor(
	private val trucksRepo : TruckRepository,
	@Database
	private val db : DocumentReference,
) : ViewModel() {

	private val query : Query = db.collection(Collection.TRUCK.value)

	val options = FirestoreRecyclerOptions.Builder<TruckDTO>().setQuery(query, TruckDTO::class.java).build()

	val uiState : MutableStateFlow<View> = MutableStateFlow(View.NONE)
	val viewState : MutableState<View> = mutableStateOf(View.NONE)

	private val _truckState : MutableStateFlow<TruckState> = MutableStateFlow(TruckState.None)
	val truckState : StateFlow<TruckState> get() = _truckState

	val truck : MutableState<Truck> = mutableStateOf(Truck())

	val rangeList = mutableStateOf(mutableStateListOf<Range>())
	val locationList = mutableStateOf(mutableStateListOf<Location>())

	private val _msgChannel = Channel<MessageEvent>()
	val msg = _msgChannel.receiveAsFlow()

	fun setTruckType(text : String) {
		truck.value.truckType.value = text
	}

	fun setAllowancePerKm(text : String) {
        truck.value.allowancePerKm.value = when (text.toDoubleOrNull()) {
            null -> truck.value.allowancePerKm.value
            else -> text
        }
    }

	/////////////////


	fun addRange() {
		rangeList.value.also {
			it.add(Range())
		}
	}

	fun removeRange(id : Int) {

		if (viewState.value == View.NEW) {
			rangeList.value.also {
				it.removeAt(id)
			}
		} else {
            if (rangeList.value[id].id == "") {
                rangeList.value.also {
                    it.removeAt(id)
                }
            } else {
                deleteRange(id)
            }
        }


	}

	private fun deleteRange(id : Int) {
		viewModelScope.launch(Dispatchers.IO) {

			trucksRepo.deleteRange(truck.value.id, rangeList.value[id].id).collect { source ->
				when (source) {
					is Source.Failure -> {
						_msgChannel.send(MessageEvent.Toast(source.ex.message ?: ""))
					}
					is Source.Success -> rangeList.value.also {
						it.removeAt(id)
					}
				}
			}
		}
	}

	fun onFromRangeChange(id : Int, fromRange : String) {
		rangeList.value.also {

            it[id].fromRange.value = when (fromRange.toIntOrNull()) {
                null -> it[id].fromRange.value
                else -> fromRange
            }
        }
	}

	fun onToRangeChange(id : Int, toRange : String) {
		rangeList.value.also {
            it[id].toRange.value = when (toRange.toIntOrNull()) {
                null -> it[id].toRange.value
                else -> toRange
            }
        }
	}

	fun onAllowanceChange(id : Int, allowance : String) {
		rangeList.value.also {
            it[id].allowance.value = when (allowance.toDoubleOrNull()) {
                null -> it[id].allowance.value
                else -> allowance
            }
        }
	}

	fun onAdditionalChange(id : Int, additional : String) {
		rangeList.value.also {
            it[id].additional.value = when (additional.toDoubleOrNull()) {
                null -> it[id].additional.value
                else -> additional
            }
        }
	}


/////////////////////


	fun addLocation() {
		locationList.value.also {
			it.add(Location())
		}
	}

	fun removeLocation(id : Int) {
		if (viewState.value == View.NEW) {
			locationList.value.also {
				it.removeAt(id)
			}
		} else {
            if (locationList.value[id].id == "") {
                locationList.value.also {
                    it.removeAt(id)
                }
            } else {
                deleteLocation(id)
            }
        }
	}

	private fun deleteLocation(id : Int) {
		viewModelScope.launch(Dispatchers.IO) {
			trucksRepo.deleteLocation(truck.value.id, locationList.value[id].id).collect { source ->
				when (source) {
					is Source.Failure -> {
						_msgChannel.send(MessageEvent.Toast("Failed to Delete"))
					}
					is Source.Success -> locationList.value.also {
						it.removeAt(id)
					}
				}
			}
		}
	}

	fun onLocationNameChange(id : Int, text : String) {
		locationList.value.also {
			it[id].locationName.value = text
		}
	}

	fun onLocationAllowanceChange(id : Int, text : String) {
		locationList.value.also {
            it[id].allowance.value = when (text.toDoubleOrNull()) {
                null -> it[id].allowance.value
                else -> text
            }
        }
	}

	//////////////////


	fun addAll() {
		viewModelScope.launch {
			when {
				truck.value.truckType.value == "" -> _msgChannel.send(MessageEvent.Toast("Enter Truck Type"))
				truck.value.allowancePerKm.value == "" -> _msgChannel.send(MessageEvent.Toast("Enter Allowance"))
				else -> {
					if (viewState.value == View.NEW) {
						addList()
					} else {
						updateList()
					}
				}
			}
		}

	}

	private suspend fun updateList() {

		withContext(Dispatchers.IO) {

			_truckState.value = TruckState.Loading("Updating")

			trucksRepo.updateTruck(
                truckDto = truck.value.toTruckDto(),
                ranges = rangeList.value.map { it.toRangeDto() },
                locations = locationList.value.map { it.toLocationDto() },
            ).collect {
				when (it) {
					is Source.Failure -> {
						_truckState.value = TruckState.Error()
						_msgChannel.send(MessageEvent.Toast(it.ex.message ?: ""))
					}
					is Source.Success -> {
						_truckState.value = TruckState.Success
						_msgChannel.send(MessageEvent.Toast("Updated"))
					}
				}
			}

		}
	}

	private suspend fun addList() {
		withContext(Dispatchers.IO) {

			_truckState.value = TruckState.Loading("Adding")

			trucksRepo.addTruck(
                truckDto = truck.value.toTruckDto(),
                ranges = rangeList.value.map { it.toRangeDto() },
                locations = locationList.value.map { it.toLocationDto() },
            ).collect {
				when (it) {
					is Source.Failure -> {
						_msgChannel.send(MessageEvent.Toast("Failed"))
						_truckState.value = TruckState.Error()
					}
					is Source.Success -> {
						_msgChannel.send(MessageEvent.Toast("Added"))
						_truckState.value = TruckState.Success
					}
				}
			}
		}

	}

	fun setArgs(truckId : String, viewType : View) {
		if (viewType == View.UPDATE) {
			truck.value.id = truckId
			getValues()
		} else if (viewType == View.NEW) {
			rangeList.value = mutableStateListOf(Range())
			locationList.value = mutableStateListOf(Location())
			_truckState.value = TruckState.New
		}

		uiState.value = viewType
		viewState.value = viewType
	}

	private fun getValues() {
		viewModelScope.launch(Dispatchers.IO) {

            _truckState.value = TruckState.Loading("Success")

            trucksRepo.getTruck(truck.value.id).collect { truck ->
                when (truck) {
                    is Resource.Failure -> {
                        _truckState.value = TruckState.Error(truck.ex.message ?: "")
                        _msgChannel.send(MessageEvent.Toast(truck.ex.message ?: ""))
                    }
                    is Resource.Success -> {
                        this@TruckViewModel.truck.value = truck.value.toTruck()
                        rangeList.value =
                            truck.value.ranges.sortedWith(compareBy { it.fromRange }).toRanges()
                        locationList.value =
                            truck.value.locations.sortedWith(compareBy { it.allowance })
                                .toLocations()
                        _truckState.value = TruckState.Got
                    }
				}
			}
		}
	}


}


sealed class TruckState {
	object New : TruckState()
	object Success : TruckState()
	object Got : TruckState()
	data class Error(val msg : String = "Unknown Error") : TruckState()
	data class Loading(val msg : String = "Loading") : TruckState()
	object None : TruckState()
}


