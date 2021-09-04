package com.mrright.distancecalc.presentation.map_fragment

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.mrright.distancecalc.R
import com.mrright.distancecalc.databinding.FragmentMapsBinding
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import com.mrright.distancecalc.presentation.states_and_events.ViewState
import com.mrright.distancecalc.utils.Dialog
import com.mrright.distancecalc.utils.constants.Search
import com.mrright.distancecalc.utils.helpers.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MapsFragment : Fragment() {

	private var _bind : FragmentMapsBinding? = null
	private val bind get() = _bind!!

	private val dialogTag = "search_fragment"

	private var googleMap : GoogleMap? = null

	private val callback = OnMapReadyCallback {

		val latLngBounds = LatLngBounds.builder().apply {
			include(LatLng(7.2513856, 103.8232222))
			include(LatLng(1.3553533, 100.4529323))
		}.build()

		it.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150))

	}

	private var originMarker : Marker? = null
	private var destinationMarker : Marker? = null
	private var searchPolyline : Polyline? = null

	private var routePolyline : MutableMap<Int, Polyline?> = mutableMapOf()

	private lateinit var requestPermissionLauncher : ActivityResultLauncher<String>

	private val viewModel by activityViewModels<MapsViewModel>()

	private lateinit var searchFragment : SearchFragment

	private lateinit var mapFragment : SupportMapFragment


	override fun onCreateView(
		inflater : LayoutInflater,
		container : ViewGroup?,
		savedInstanceState : Bundle?,
	) : View? {
		_bind = FragmentMapsBinding.inflate(inflater, container, false)
		return _bind?.root
	}


	private fun initMap() {
		mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
	}


	override fun onViewCreated(
		view : View,
		savedInstanceState : Bundle?,
	) {
		super.onViewCreated(view, savedInstanceState)

		init()
		initMap()
		clicks()
		collectViewState()
		collectMsg()
		collectMapState()
		collectPolyLines()

		collectFromText()
		collectToText()

		collectKm()
		collectAllowance()
		collectRoundUpAllowance()
		collectTruckName()
		collectLocationAllowance()

	}


	private fun collectTruckName() {
		lifecycleScope.launchWhenCreated {
			viewModel.truckName.observe(viewLifecycleOwner) {
				bind.spinTruck.setText(it)
			}
		}
	}


	private fun registerActions() {
		requestPermissionLauncher = requireActivity().registerForActivityResult(ActivityResultContracts.RequestPermission()) {
			if (it) {
				checkGpsStatus()
			} else {
				requestPermission()
			}
		}
	}


	private fun collectViewState() {
		lifecycleScope.launchWhenCreated {
			viewModel.viewState.collect {
				when (it) {
					ViewState.Init -> {
						registerActions()
						viewModel.changeViewState(ViewState.None)
						checkPermission()
					}
					ViewState.None -> Unit
				}
			}
		}
	}


	private fun collectMsg() {
		lifecycleScope.launchWhenCreated {
			viewModel.msgChannel.collect {
				when (it) {
					is MessageEvent.SnackBar -> Unit
					is MessageEvent.Toast -> requireContext().toast(it.msg, it.duration)
					is MessageEvent.ToastStringRes -> Unit
				}
			}
		}
	}


	private fun collectRoundUpAllowance() {
		lifecycleScope.launchWhenCreated {
			viewModel.roundUpAllowance.observe(viewLifecycleOwner) {
				bind.txtRoundUpAllowance.text = "RoundUp Allowance : $it"
			}
		}
	}


	private fun collectAllowance() {
		lifecycleScope.launchWhenCreated {
			viewModel.allowance.observe(viewLifecycleOwner) {
				bind.txtAllowance.text = "Allowance : $it"
			}
		}
	}


	private fun collectKm() {
		lifecycleScope.launchWhenCreated {
			viewModel.kmInText.observe(viewLifecycleOwner) {
				bind.txtTotalKm.text = "Total Km : $it "
				bind.txtRoute.setText("$it km")
			}
		}
	}


	private fun collectLocationAllowance() {
		lifecycleScope.launchWhenCreated {
			viewModel.locationAllowance.observe(viewLifecycleOwner) {
				bind.txtLocationAllowance.text = "Location Allowance : $it"
			}
		}
	}


	private fun collectMapState() {
		lifecycleScope.launchWhenCreated {
			viewModel.mapState.collect {
				with(bind) {

					when (it) {
						is MapState.Init -> {
							cardSearch.gone()
							cardResult.gone()
							fabSearch.visible()
							originMarker?.remove()
							destinationMarker?.remove()
							searchPolyline?.remove()
							for ((_, line) in routePolyline) {
								line?.remove()
							}
						}
						is MapState.Search -> {
							cardSearch.visible()
							cardResult.gone()
							fabSearch.gone()
						}
						is MapState.SearchResult -> {
							cardSearch.gone()
							cardResult.visible()
							fabSearch.visible()
						}
						is MapState.ChangeRoute -> {
							originMarker?.remove()
							destinationMarker?.remove()
							searchPolyline?.remove()
							for ((_, line) in routePolyline) {
								line?.remove()
							}
						}
					}
				}
			}
		}
	}


	private fun collectPolyLines() {


		lifecycleScope.launchWhenCreated {
			viewModel.polylineOptionsList.observe(viewLifecycleOwner) { polylines ->
				if (polylines != null) {

					val latLngBounds = LatLngBounds.builder().apply {
						include(polylines.origin?.latLng!!)
						include(polylines.destination?.latLng!!)
					}.build()

					val originOptions = MarkerOptions().apply {
						position(polylines.origin?.latLng!!).title(polylines.origin?.address)
					}

					val destinationOptions = MarkerOptions().apply {
						position(polylines.destination?.latLng!!).title(polylines.destination?.address)
					}

					val routes = OnMapReadyCallback { map ->

						originMarker = map.addMarker(originOptions)
						destinationMarker = map.addMarker(destinationOptions)
						for ((index, route) in polylines.routes) {
							routePolyline[index] = map.addPolyline(route.polylineOptions?.also {

								it.color(
									if (route.isSelected) {
										ContextCompat.getColor(requireContext(), R.color.quantum_googred400)
									} else {
										ContextCompat.getColor(requireContext(), R.color.map_grey)
									}
								)
							}!!)
						}

						map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150))

					}

					mapFragment.getMapAsync(routes)


				}
			}
		}


	}

	private fun collectToText() {
		lifecycleScope.launchWhenCreated {
			viewModel.toLocationText.observe(viewLifecycleOwner) {
				bind.etToLocation.setText(it)
			}
		}
	}


	private fun collectFromText() {
		lifecycleScope.launchWhenCreated {
			viewModel.fromLocationText.observe(viewLifecycleOwner) {
				bind.etFromLocation.setText(it)
			}
		}
	}


	private fun clicks() {

		with(bind) {

			fabSearch.setOnClickListener {
				viewModel.changeMapState(MapState.Search)
			}

			etFromLocation.setOnFocusChangeListener { _, isFocused ->
				if (isFocused) {
					searchFragment = SearchFragment.newInstance(Search.FROM, bind.etFromLocation.text.toString())
					searchFragment.show(childFragmentManager, dialogTag)
				}
			}

			etToLocation.setOnFocusChangeListener { _, isFocused ->
				if (isFocused) {
					searchFragment = SearchFragment.newInstance(Search.TO, bind.etToLocation.text.toString())
					searchFragment.show(childFragmentManager, dialogTag)
				}
			}

			btnGetRoute.setOnClickListener {
				originMarker?.remove()
				destinationMarker?.remove()
				searchPolyline?.remove()
				for ((_, line) in routePolyline) {
					line?.remove()
				}
				lifecycleScope.launch { viewModel.validate() }

			}

			imgClose.setOnClickListener {
				viewModel.changeMapState()
			}

			imgResultClose.setOnClickListener {
				viewModel.changeMapState()
			}

			txtRoute.setOnClickListener {

				val list = viewModel.routeList.map { it.toString() }.toTypedArray()

				val dialog = Dialog.selectableDialog(
					context = requireContext(),
					list = list,
					checkedItem = viewModel.selectedRouteId,
					title = "Select Route",
				) { dialogInterface, id ->
					viewModel.changeSelectedRoute(id)
					dialogInterface.dismiss()
					txtRoute.clearFocus()
					txtInputTruck.clearFocus()
				}
				dialog.show()
			}

			spinTruck.setOnClickListener {

				val list = mutableListOf<String>()

				viewModel.truckList.forEach {
					list.add(it.truckType)
				}

				val dialog = Dialog.selectableDialog(
					context = requireContext(),
					list = list.toTypedArray(),
					checkedItem = viewModel.selectTruckId,
					title = "Select Truck",
				) { dialogInterface, id ->
					viewModel.changeSelectedTruck(id)
					dialogInterface.dismiss()
					spinTruck.clearFocus()
					inputTxtTruck.clearFocus()
				}
				dialog.show()
			}


		}
	}


	private fun checkPermission() {
		if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
			requestPermission()
		} else {
			enableGps()
		}
	}

	private fun init() {

		bind.etFromLocation.apply {
			keyListener = null
			clearFocus()
		}

		bind.etToLocation.apply {
			keyListener = null
			clearFocus()
		}

		bind.txtRoute.apply {
			keyListener = null
			clearFocus()
		}


	}

	private fun checkGpsStatus() {
		if (!requireContext().isGpsTurnedOn()) {
			enableGps()
		}
	}

	private fun enableGps() {

		val result : Task<LocationSettingsResponse> =
			LocationServices.getSettingsClient(requireContext()).checkLocationSettings(locationSettingsRequest)

		result.addOnSuccessListener {
			requireContext().toast("Turned On")
		}.addOnFailureListener { exception ->
			val ex = exception as ApiException
			when (ex.statusCode) {
				LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> { //popup for enabling GPS
					val resolvableApiException = ex as ResolvableApiException
					resolvableApiException.startResolutionForResult(requireActivity(), 111)
				}
				LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
					requireContext().toast(ex.message!!)
				}
			}
		}
	}

	private fun requestPermission() {
		requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
	}


	override fun onDestroyView() {
		super.onDestroyView()
		originMarker?.remove()
		destinationMarker?.remove()
		searchPolyline?.remove()
		for ((_, line) in routePolyline) {
			line?.remove()
		}
		_bind = null
	}


}














