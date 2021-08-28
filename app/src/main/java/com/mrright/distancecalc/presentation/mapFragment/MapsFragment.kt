package com.mrright.distancecalc.presentation.mapFragment

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.tasks.Task
import com.mrright.distancecalc.R
import com.mrright.distancecalc.databinding.FragmentMapsBinding
import com.mrright.distancecalc.presentation.Dialog
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

	private val callback = OnMapReadyCallback { googleMap ->

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

		initMap()

		return _bind?.root
	}

	private fun initMap() {
		mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
		mapFragment.getMapAsync(callback)
	}


	override fun onViewCreated(
		view : View,
		savedInstanceState : Bundle?,
	) {
		super.onViewCreated(view, savedInstanceState)


		init()

		checkPermission()
		clicks()

		collectMsg()

		collectMapState()
		collectPolyLines()

		collectFromText()
		collectToText()

		collectKm()
		collectAllowance()
		collectRoundUpAllowance()

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
				bind.txtRoundUpAllowance.text = it
			}
		}
	}

	private fun collectAllowance() {
		lifecycleScope.launchWhenCreated {
			viewModel.allowanceText.observe(viewLifecycleOwner) {
				bind.txtAllowance.text = it
			}
		}
	}

	private fun collectKm() {
		lifecycleScope.launchWhenCreated {
			viewModel.kmInText.observe(viewLifecycleOwner) {
				bind.txtTotalKm.text = "Total Km : ${it.dropLast(3)}"
				bind.txtRoute.setText(it)
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
							initSpinner()
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

					val latLngBounds = LatLngBounds.builder()
						.apply {
							include(polylines.origin?.latLng!!)
							include(polylines.destination?.latLng!!)
						}
						.build()

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

								it.color(if (route.isSelected) {
									ContextCompat.getColor(requireContext(), R.color.quantum_googred400)
								}
								else {
									ContextCompat.getColor(requireContext(), R.color.map_grey)
								})
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


	private fun initSpinner() {
		val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, viewModel.items)
		(bind.spinTruck as? AutoCompleteTextView)?.setAdapter(adapter)
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

				//if (isFocused) {
				val list = viewModel.routesList.toTypedArray()

				val dialog = Dialog.selectableDialog(
					context = requireContext(),
					list = list,
					checkedItem = list.indexOf(viewModel.selectedRoute.value),
					title = "Select Route",
				) { dialogInterface, id ->
					viewModel.changeSelectedRoute(id)
					dialogInterface.dismiss()
					txtRoute.clearFocus()
					txtInputTruck.clearFocus()
				}

				dialog.show()
				//}
			}

			spinTruck.setOnItemClickListener { adapterView, view, i, l ->
				viewModel.changeSelectedTruck(i)
			}


		}
	}


	private fun checkPermission() {
		if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
			requestPermission()
		}
		else {
			enableGps()
		}
	}

	private fun init() {

		requestPermissionLauncher = requireActivity().registerForActivityResult(ActivityResultContracts.RequestPermission()) {
			if (it) {
				checkGpsStatus()
			}
			else {
				requestPermission()
			}
		}

		bind.etFromLocation.apply {
			keyListener = null
		}
		bind.etToLocation.apply {
			keyListener = null
		}

		bind.txtRoute.apply {
			keyListener = null
		}

	}

	private fun checkGpsStatus() {
		if (!requireContext().isGpsTurnedOn()) {
			enableGps()
		}
	}

	private fun enableGps() {

		val result : Task<LocationSettingsResponse> = LocationServices.getSettingsClient(requireContext())
			.checkLocationSettings(locationSettingsRequest)

		result.addOnSuccessListener {
			requireContext().toast("Turned On")
		}
			.addOnFailureListener { exception ->
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
		_bind = null
	}


}














