package com.mrright.distancecalc.presentation.mapFragment

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
import com.mrright.distancecalc.models.Details
import com.mrright.distancecalc.models.Polylines
import com.mrright.distancecalc.models.Route
import com.mrright.distancecalc.utils.constants.Alternatives
import com.mrright.distancecalc.utils.constants.Mode
import com.mrright.distancecalc.utils.constants.Search
import com.mrright.distancecalc.utils.constants.Truck
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
import kotlin.math.ceil

@HiltViewModel
class MapsViewModel @Inject constructor(
	private val mapsRepository : MapsRepository,
	private val placesRepository : PlacesRepository,
) : ViewModel() {

	val items = Truck.values()
		.toMutableList()

	private val _msgChannel = Channel<MessageEvent>()
	val msgChannel = _msgChannel.receiveAsFlow()

	private val _selectedTruck : MutableLiveData<Truck> = MutableLiveData(null)

	private val _kmInText : MutableLiveData<String> = MutableLiveData("")
	val kmInText : LiveData<String> get() = _kmInText

	private val _allowance : MutableLiveData<String> = MutableLiveData("")
	val allowance : LiveData<String> get() = _allowance

	private val _allowanceText : MutableLiveData<String> = MutableLiveData("")
	val allowanceText : LiveData<String> get() = _allowanceText

	private val _roundUpAllowance : MutableLiveData<String> = MutableLiveData("")
	val roundUpAllowance : LiveData<String> get() = _roundUpAllowance

	private val _mapState : MutableStateFlow<MapState> = MutableStateFlow(MapState.Init)
	val mapState : StateFlow<MapState> get() = _mapState

	private val _fromLocationText : MutableLiveData<String> = MutableLiveData(null)
	val fromLocationText : LiveData<String> get() = _fromLocationText

	private val _toLocationText : MutableLiveData<String> = MutableLiveData(null)
	val toLocationText : LiveData<String> get() = _toLocationText

	private val _fromPlace : MutableLiveData<Place> = MutableLiveData(null)
	val fromPlace : LiveData<Place> get() = _fromPlace

	private val _toPlace : MutableLiveData<Place> = MutableLiveData(null)
	val toPlace : LiveData<Place> get() = _toPlace

	private val _directionsLiveData : MutableLiveData<DirectionsDTO> = MutableLiveData(null)
	val directionsLiveData : LiveData<DirectionsDTO> get() = _directionsLiveData

	private var _polylineOptionsList : MutableLiveData<Polylines?> = MutableLiveData(null)
	val polylineOptionsList : LiveData<Polylines?> get() = _polylineOptionsList

	var routesList : MutableList<String> = mutableListOf()

	private var _selectedRoute : MutableLiveData<String> = MutableLiveData(null)
	val selectedRoute : LiveData<String> get() = _selectedRoute

	private var shortestPath : Int = -1

	private val _autoCompletePredict : MutableLiveData<MutableList<AutocompletePrediction>> = MutableLiveData(null)
	val autoCompletePredict : LiveData<MutableList<AutocompletePrediction>> get() = _autoCompletePredict

	fun changeMapState(mapState : MapState = MapState.Init) {

		_mapState.value = mapState

		when (mapState) {
			MapState.Init -> {
				_autoCompletePredict.value = null
				_fromPlace.value = null
				_toPlace.value = null
				_fromLocationText.value = null
				_toLocationText.value = null
				_polylineOptionsList.value = null
				routesList.clear()
				_selectedRoute.value = ""
				shortestPath = -1
				_kmInText.value = ""
				_allowanceText.value = ""
				_roundUpAllowance.value = ""
				_selectedRoute.value = null
			}
			MapState.Search -> {
				routesList.clear()
			}
			MapState.SearchResult -> {
				_autoCompletePredict.value = null
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
					_autoCompletePredict.value = null
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
		if (search == Search.FROM) {
			_fromLocationText.value = placeText
		}
		else {
			_toLocationText.value = placeText
		}

		viewModelScope.launch(Main) {

			val request = FetchPlaceRequest.builder(placeId, placeFields)
				.build()

			val result = withContext(IO) {
				placesRepository.fetchPlace(request)
			}

			result.collect {
				when (it) {
					is Resource.Failure -> {

						if (search == Search.FROM) {
							_fromPlace.value = null
						}
						else {
							_toPlace.value = null
						}

					}
					is Resource.Success -> {

						if (search == Search.FROM) {
							_fromPlace.value = it.value
						}
						else {
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
			_selectedTruck.value == null -> {
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

							shortestPath = list.minOrNull()!!

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

									if (shortestPath == legs?.get(0)?.distance?.value) {
										_selectedRoute.value = legs[0].distance?.text!!
									}


									routesList.add(legs?.get(0)?.distance?.text!!)

									this[index] = Route(
										id = index,
										distance = Distance(
											text = legs.get(0).distance?.text ?: "",
											value = legs.get(0).distance?.value ?: 0,
										),
										duration = legs.get(0).duration?.text ?: "",
										polylineOptions = PolylineOptions().apply {
											width(8F)
											startCap(ButtCap())
											jointType(JointType.ROUND)
											addAll(decodePolyline(route.overviewPolyline?.points!!))
										},
										isSelected = shortestPath == legs.get(0).distance?.value,
									)
								}
							})

							_directionsLiveData.value = data.value
							_polylineOptionsList.value = polylines
							changeMapState(MapState.SearchResult)
							calculateAllowance()


						}
						is Resource.Failure -> {
							_polylineOptionsList.value = null
							_directionsLiveData.value = null
						}
					}
				}
			}


		}
	}

	fun setAutoPredictToNull() {
		_autoCompletePredict.value = null
	}


	fun changeSelectedRoute(id : Int) {
		_selectedRoute.value = routesList[id]
		_mapState.value = MapState.ChangeRoute
		_polylineOptionsList.value = _polylineOptionsList.value.let {

			for ((index, route) in it?.routes!!) {
				route.isSelected = id == index
			}
			_mapState.value = MapState.SearchResult
			it
		}

		calculateAllowance()
	}

	fun changeSelectedTruck(id : Int) {
		_selectedTruck.value = items[id]
		calculateAllowance()
	}

	private fun calculateAllowance() {

		if (selectedRoute.value != null && _selectedTruck.value != null) {

			val kmInInt = selectedRoute.value?.dropLast(3)

			val kmInt = kmInInt?.toInt() ?: 0

			val truckAllowance = _selectedTruck.value?.allowancePerKm

			val allowance = kmInt.times(truckAllowance!!)

			_kmInText.value = selectedRoute.value
			_allowanceText.value = "Allowance : $kmInInt * $truckAllowance = ${allowance.toFloat()}"
			_roundUpAllowance.value = "RoundUp Allowance : ${ceil(allowance).toInt()}"

		}
	}


}