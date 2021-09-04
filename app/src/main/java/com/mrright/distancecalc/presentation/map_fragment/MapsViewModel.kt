package com.mrright.distancecalc.presentation.map_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.mrright.distancecalc.data.Resource
import com.mrright.distancecalc.data.api.repositories.MapsRepository
import com.mrright.distancecalc.data.api.repositories.PlacesRepository
import com.mrright.distancecalc.data.api.responses.DirectionsDTO
import com.mrright.distancecalc.data.api.responses.Distance
import com.mrright.distancecalc.data.firestore.TruckRepository
import com.mrright.distancecalc.data.firestore.models.LocationDto
import com.mrright.distancecalc.data.firestore.models.RangeDto
import com.mrright.distancecalc.data.firestore.models.TruckDto
import com.mrright.distancecalc.models.Details
import com.mrright.distancecalc.models.Polylines
import com.mrright.distancecalc.models.Route
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import com.mrright.distancecalc.presentation.states_and_events.ViewState
import com.mrright.distancecalc.utils.constants.Alternatives
import com.mrright.distancecalc.utils.constants.Mode
import com.mrright.distancecalc.utils.constants.Search
import com.mrright.distancecalc.utils.helpers.decodePolyline
import com.mrright.distancecalc.utils.helpers.placeFields
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
	private val mapsRepository : MapsRepository,
	private val placesRepository : PlacesRepository,
	private val truckRepo : TruckRepository,
) : ViewModel() {

	private val _msgChannel = Channel<MessageEvent>()
	val msgChannel = _msgChannel.receiveAsFlow()

	private val _viewState : MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Init)
	val viewState : StateFlow<ViewState> get() = _viewState

	private val _mapState : MutableStateFlow<MapState> = MutableStateFlow(MapState.Init)
	val mapState : StateFlow<MapState> get() = _mapState

	private val _autoCompletePredict : MutableLiveData<List<AutocompletePrediction>> = MutableLiveData(listOf())
	val autoCompletePredict : LiveData<List<AutocompletePrediction>> get() = _autoCompletePredict

	private val _fromLocationText : MutableLiveData<String> = MutableLiveData("")
	val fromLocationText : LiveData<String> get() = _fromLocationText

	private val _toLocationText : MutableLiveData<String> = MutableLiveData("")
	val toLocationText : LiveData<String> get() = _toLocationText

	private var selectedTruck : TruckDto? = null
	var truckList : List<TruckDto> = listOf()
	var selectTruckId = 0

	private var selectedRoute : Int? = null
	var routeList : MutableList<Int> = mutableListOf()
	var selectedRouteId = 0
	private var shortestRoute : Int = 0


	private var range : RangeDto? = null

	private var locations = listOf<LocationDto>()

	private var truck : TruckDto? = null


	private val _kmInText : MutableLiveData<String> = MutableLiveData("")
	val kmInText : LiveData<String> get() = _kmInText

	private val _truckName : MutableLiveData<String> = MutableLiveData("")
	val truckName : LiveData<String> get() = _truckName

	private val _locationAllowance : MutableLiveData<String> = MutableLiveData("")
	val locationAllowance : LiveData<String> get() = _locationAllowance

	private val _allowance : MutableLiveData<String> = MutableLiveData("")
	val allowance : LiveData<String> get() = _allowance

	private val _roundUpAllowance : MutableLiveData<String> = MutableLiveData("")
	val roundUpAllowance : LiveData<String> get() = _roundUpAllowance


	private val _fromPlace : MutableLiveData<Place> = MutableLiveData(null)
	private val fromPlace : LiveData<Place> get() = _fromPlace

	private val _toPlace : MutableLiveData<Place> = MutableLiveData(null)
	private val toPlace : LiveData<Place> get() = _toPlace

	private val _directionsLiveData : MutableLiveData<DirectionsDTO> = MutableLiveData(null)
	val directionsLiveData : LiveData<DirectionsDTO> get() = _directionsLiveData

	private var _polylineOptionsList : MutableLiveData<Polylines?> = MutableLiveData(null)
	val polylineOptionsList : LiveData<Polylines?> get() = _polylineOptionsList


	init {
		getTrucksCallBack()
	}


	fun changeMapState(mapState : MapState = MapState.Init) {

		_mapState.value = mapState

		when (mapState) {
			MapState.Init -> {
				_autoCompletePredict.value = listOf()
				_directionsLiveData.value = null
				_fromPlace.value = null
				_toPlace.value = null
				_fromLocationText.value = ""
				_toLocationText.value = ""
				_polylineOptionsList.value = null
				routeList.clear()
				shortestRoute = -1
				_kmInText.value = ""
				_allowance.value = ""
				_roundUpAllowance.value = ""
				_locationAllowance.value = ""
				range = null
				locations = listOf()
				truck = null
			}
			MapState.Search -> {
				routeList.clear()
			}
			MapState.SearchResult -> {
				_autoCompletePredict.value = listOf()
			}
			else -> Unit
		}
	}

	fun placesAutoCompletePredicts(predictionsRequest : FindAutocompletePredictionsRequest) {
		viewModelScope.launch(Main) {

			delay(1000L)

			val result = withContext(IO) {
				placesRepository.placesAutoCompletePredicts(predictionsRequest)
			}

			when (result) {
				is Resource.Failure -> {
					_autoCompletePredict.value = listOf()
				}
				is Resource.Success -> {
					_autoCompletePredict.value = result.value
				}
			}

		}
	}

	fun fetchPlace(
		search : Search,
		placeId : String,
		placeText : String,
	) {

		viewModelScope.launch(Main) {

			if (search == Search.FROM) {
				_fromLocationText.value = placeText
			} else {
				_toLocationText.value = placeText
			}

			val request = FetchPlaceRequest.builder(placeId, placeFields).build()

			val result = withContext(IO) {
				placesRepository.fetchPlace(request)
			}

			result.collect {
				when (it) {
					is Resource.Failure -> {

						if (search == Search.FROM) {
							_fromPlace.value = null
						} else {
							_toPlace.value = null
						}

					}
					is Resource.Success -> {

						if (search == Search.FROM) {
							_fromPlace.value = it.value
						} else {
							_toPlace.value = it.value
						}


					}
				}
			}
		}
	}


	suspend fun validate() {

		when {
			_fromPlace.value == null -> {
				_msgChannel.send(MessageEvent.Toast("Enter From Location"))
			}
			toPlace.value == null -> {
				_msgChannel.send(MessageEvent.Toast("Enter To Location"))
			}
			selectedTruck == null -> {
				_msgChannel.send(MessageEvent.Toast("Choose truck"))
			}
			else -> {
				getDirection()
			}
		}
	}

	private fun getDirection() {
		viewModelScope.launch(Main) {

			if (fromPlace.value != null && toPlace.value != null) {

				val result = withContext(IO) {

					mapsRepository.getDirection(
						mode = Mode.DRIVING.value,
						origin = _fromPlace.value?.latLng!!,
						alternatives = Alternatives.TRUE.value,
						destination = _toPlace.value?.latLng!!,
					)
				}

				result.collect { data ->

					when (data) {
						is Resource.Success -> {

							val map : MutableMap<Int, Route> = mutableMapOf()

							val direction = data.value
							val geoCodeWayPoints = direction.geocodedWaypoints
							val routes = direction.routes

							val list : MutableList<Int> = mutableListOf()

							routes?.forEach { route ->
								list.add(route.legs?.get(0)?.distance?.value!!)
							}

							shortestRoute = list.minOrNull()!!

							val polylines = Polylines(origin = Details(
								placeId = geoCodeWayPoints?.get(0)?.placeId ?: "",
								latLng = routes?.get(0)?.legs?.get(0)?.startLocation?.let {
									LatLng(it.lat!!, it.lng!!)
								},
								address = routes?.get(0)?.legs?.get(0)?.startAddress ?: "",
							), destination = Details(
								placeId = geoCodeWayPoints?.get(1)?.placeId ?: "",
								latLng = routes?.get(0)?.legs?.get(0)?.endLocation?.let {
									LatLng(it.lat!!, it.lng!!)
								},
								address = routes?.get(0)?.legs?.get(0)?.endAddress ?: "",
							), routes = map.apply {
								routes?.forEachIndexed { index, route ->

									val legs = route.legs

									val distance = legs?.get(0)?.distance?.text!!
									val kmInDouble = distance.dropLast(3).replace(",", "").toFloat()
									val kmInInt = kmInDouble.toInt()

									if (shortestRoute == legs[0].distance?.value) {
										selectedRoute = kmInInt
										selectedRouteId = index
										val s = ""
									}

									routeList.add(kmInInt)

									this[index] = Route(
										id = index,
										distance = Distance(
											text = legs[0].distance?.text ?: "",
											value = legs[0].distance?.value ?: 0,
										),
										duration = legs[0].duration?.text ?: "",
										polylineOptions = PolylineOptions().apply {
											width(8F)
											startCap(ButtCap())
											jointType(JointType.ROUND)
											addAll(decodePolyline(route.overviewPolyline?.points!!))
										},
										isSelected = shortestRoute == legs[0].distance?.value,
									)
								}
							})

							_directionsLiveData.value = data.value
							_polylineOptionsList.value = polylines
							changeMapState(MapState.SearchResult)
							checkBothSelected()

						}
						is Resource.Failure -> {
							_polylineOptionsList.value = null
							_directionsLiveData.value = null
							routeList.clear()
							selectedRoute = 0
							shortestRoute = 0
						}
					}
				}
			}


		}
	}

	fun setAutoPredictToNull() {
		_autoCompletePredict.value = listOf()
	}


	fun changeSelectedRoute(id : Int) {

		selectedRoute = routeList[id]
		selectedRouteId = id

		_mapState.value = MapState.ChangeRoute
		_polylineOptionsList.value = _polylineOptionsList.value.let {

			for ((index, route) in it?.routes!!) {
				route.isSelected = id == index
			}
			_mapState.value = MapState.SearchResult
			it
		}

		checkBothSelected()
	}

	fun changeSelectedTruck(id : Int) {

		selectedTruck = truckList[id]
		selectTruckId = id
		_truckName.value = truckList[id].truckType

		checkBothSelected()

	}

	private fun checkBothSelected() {
		viewModelScope.launch {
			if (selectedRoute != null && selectedTruck != null) {
				getTruck()
				getRange()
				getLocation()
			}
		}
	}

	private fun getTruck() {
		viewModelScope.launch(IO) {
			truckRepo.getOnlyTruck(selectedTruck?.id!!).collect {
				when (it) {
					is Resource.Failure -> {
						truck = null
					}
					is Resource.Success -> {
						truck = it.value
						calculateAllowance()
					}
				}
			}
		}
	}

	private fun getLocation() {
		viewModelScope.launch(IO) {
			truckRepo.getLocation(
				truckId = selectedTruck!!.id,
				from = fromPlace.value?.address?.substringAfterLast(",")?.trim()!!,
				to = toPlace.value?.address?.substringAfterLast(",")?.trim()!!,
			).collect {
				when (it) {
					is Resource.Failure -> {
						locations = listOf()
						_msgChannel.send(MessageEvent.Toast(it.ex.message ?: ""))
					}
					is Resource.Success -> {
						locations = it.value
						calculateAllowance()
					}
				}
			}
		}
	}

	private fun getRange() {
		viewModelScope.launch(IO) {

			val truckId = selectedTruck!!.id
			val route = selectedRoute!!

			truckRepo.getRange(truckId, route).collect {
				when (it) {
					is Resource.Failure -> {
						range = null
						_msgChannel.send(MessageEvent.Toast(it.ex.message ?: ""))
					}
					is Resource.Success -> {
						range = it.value
						calculateAllowance()
					}
				}
			}
		}
	}


	private fun calculateAllowance() {
		viewModelScope.launch(Main) {

			if (selectedRoute != null && selectedTruck != null) {

				val km = selectedRoute ?: 0
				val allowancePerKm = truck?.allowancePerKm ?: 0.0

				_kmInText.value = km.toString()

				_allowance.value = " $km * $allowancePerKm = ${km.times(allowancePerKm).toFloat()} "

				if (range != null) {

					if (range!!.allowance != 0.0) {
						var value = range!!.allowance.plus(range!!.additional)

						locations.forEach {
							value = value.plus(it.allowance)
						}

						if (locations.isNotEmpty()) {
							_locationAllowance.value = locations[0].allowance.toString()
						}

						_roundUpAllowance.value = value.toInt().toString()
					} else {

						val roundUp = km.times(allowancePerKm).plus(range!!.additional)

						_roundUpAllowance.value = roundUp.toInt().toString()
					}

				} else {

					var value = 0.0

					locations.forEach {
						value = value.plus(it.allowance)
					}

					_locationAllowance.value = value.toString()

					_roundUpAllowance.value = km.times(allowancePerKm).plus(value).toInt().toString()


				}

			}

		}

	}

	fun changeViewState(state : ViewState) {
		_viewState.value = state
	}


	private fun getTrucksCallBack() {
		viewModelScope.launch(IO) {
			truckRepo.getTrucksCallBack().collect {
				when (it) {
					is Resource.Failure -> {
						truckList = listOf()
						_msgChannel.send(MessageEvent.Toast(it.ex.message ?: ""))
					}
					is Resource.Success -> {
						truckList = it.value
					}
				}
			}
		}
	}


}