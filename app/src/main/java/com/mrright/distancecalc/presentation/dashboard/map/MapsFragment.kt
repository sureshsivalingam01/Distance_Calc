package com.mrright.distancecalc.presentation.dashboard.map

import android.Manifest
import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mrright.distancecalc.R
import com.mrright.distancecalc.databinding.FragmentMapsBinding
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import com.mrright.distancecalc.utils.constants.Save
import com.mrright.distancecalc.utils.constants.Search
import com.mrright.distancecalc.utils.helpers.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@AndroidEntryPoint
class MapsFragment : Fragment() {

    private var _bind: FragmentMapsBinding? = null
    private val bind get() = _bind!!

    private val dialogTag = "search_fragment"

    private lateinit var locationCallback: LocationCallback

    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var searchPolyline: Polyline? = null

    private var routePolyline: MutableMap<Int, Polyline?> = mutableMapOf()

    private val viewModel by activityViewModels<MapsViewModel>()

    private lateinit var searchFragment: SearchFragment

    private lateinit var mapFragment: SupportMapFragment

    @Inject
    lateinit var providerClient: FusedLocationProviderClient

    @Inject
    lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _bind = FragmentMapsBinding.inflate(inflater, container, false)
        return _bind?.root
    }


    private fun initMap() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        getCurrentLocate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {

                val latLng =
                    LatLng(result.locations.last().latitude, result.locations.last().longitude)

                if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
                    getGeoCoder(latLng)
                }

            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initMap()
        clicks()

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

        collectBounds()

    }

    private fun collectBounds() {
        lifecycleScope.launchWhenCreated {
            viewModel.boundFlow.collect { bounds ->

                if (bounds != null) {

                    if (viewModel.mapState.value == MapState.Init) {
                        val b = LatLngBounds.builder().apply {
                            this.include(
                                LatLng(
                                    bounds.northeast?.latitude!!,
                                    bounds.northeast?.longitude!!
                                )
                            )
                            this.include(
                                LatLng(
                                    bounds.southwest?.latitude!!,
                                    bounds.southwest?.longitude!!
                                )
                            )
                        }.build()

                        val callback = OnMapReadyCallback {
                            it.animateCamera(CameraUpdateFactory.newLatLngBounds(b, 0))
                        }
                        mapFragment.getMapAsync(callback)
                    }

                }
            }
        }
    }

    private fun getCurrentLocate() {
        if (isPermissionGranted(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && isPermissionGranted(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            getLocate()
        }

    }


    private fun collectTruckName() {
        lifecycleScope.launchWhenCreated {
            viewModel.truckName.observe(viewLifecycleOwner) {
                bind.spinTruck.setText(it)
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun getLocate() {
        if (isPermissionGranted(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) && isPermissionGranted(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {

            providerClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    getGeoCoder(LatLng(it.latitude, it.longitude))
                } else {
                    requestForLocationUpdates()
                }
            }.addOnFailureListener {
                requestForLocationUpdates()
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun requestForLocationUpdates() {
        providerClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }


    private fun getGeoCoder(latLng: LatLng) {
        lifecycleScope.launch(Dispatchers.Main) {
            providerClient.removeLocationUpdates(locationCallback)
            val countryName = withContext(IO) {
                viewModel.getCountryName(latLng)
            }
            viewModel.getBounds(countryName)
        }
    }


    private fun collectMsg() {
        lifecycleScope.launchWhenCreated {
            viewModel.msgChannel.collect {
                when (it) {
                    is MessageEvent.Toast -> toast(it.msg, it.duration)
                    else -> Unit
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
                bind.txtRoute.setText("$it Km")
            }
        }
    }


    private fun collectLocationAllowance() {
        lifecycleScope.launchWhenCreated {
            viewModel.locationAllowance.observe(viewLifecycleOwner) {
                bind.txtLocationAllowance.text = "Location Allowance : ${it ?: 0}"
            }
        }
    }


    private fun collectMapState() {
        lifecycleScope.launchWhenCreated {
            viewModel.mapState.collect {
                with(bind) {

                    when (it) {
                        is MapState.Init -> {
                            fabSearch.visible()
                            fabResult.gone()
                            cardSearch.gone()
                            cardResult.gone()
                            originMarker?.remove()
                            destinationMarker?.remove()
                            searchPolyline?.remove()
                            for ((_, line) in routePolyline) {
                                line?.remove()
                            }
                        }
                        is MapState.Search -> {
                            fabResult.gone()
                            fabSearch.gone()
                            cardSearch.visible()
                            cardResult.gone()
                        }
                        is MapState.Result -> {
                            fabResult.gone()
                            fabSearch.gone()
                            cardSearch.gone()
                            cardResult.visible()
                        }
                        is MapState.ChangeRoute -> {
                            originMarker?.remove()
                            destinationMarker?.remove()
                            searchPolyline?.remove()
                            for ((_, line) in routePolyline) {
                                line?.remove()
                            }
                        }
                        is MapState.FabResult -> {
                            fabSearch.gone()
                            fabResult.gone()
                            cardSearch.gone()
                            cardResult.visible()
                        }
                        is MapState.FabSearch -> {
                            fabSearch.gone()
                            fabResult.gone()
                            cardSearch.visible()
                            cardResult.gone()
                        }
                        is MapState.Close -> {
                            fabSearch.visible()
                            fabResult.gone()
                            cardSearch.gone()
                            cardResult.gone()
                        }
                        is MapState.ResultInit -> {
                            fabSearch.visible()
                            fabResult.visible()
                            cardSearch.gone()
                            cardResult.gone()
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
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.quantum_googred400
                                        )
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

    private fun fromLocation(view: View? = null) {
        searchFragment =
            SearchFragment.newInstance(Search.FROM, bind.etFromLocation.text.toString())
        searchFragment.show(childFragmentManager, dialogTag)
    }

    private fun toLocation(view: View? = null) {
        searchFragment = SearchFragment.newInstance(Search.TO, bind.etToLocation.text.toString())
        searchFragment.show(childFragmentManager, dialogTag)
    }


    private fun clicks() {

        with(bind) {

            fabResult.setOnClickListener {

                if (viewModel.polylineOptionsList.value == null) {
                    viewModel.changeMapState(MapState.Result)
                } else {
                    viewModel.changeMapState(MapState.FabResult)
                }
            }

            fabSearch.setOnClickListener {
                if (viewModel.polylineOptionsList.value == null) {
                    viewModel.changeMapState(MapState.Search)
                } else {
                    viewModel.changeMapState(MapState.FabSearch)
                }
            }

            etFromLocation.setOnFocusChangeListener { _, b ->
                if (b) {
                    fromLocation()
                }
            }

            etFromLocation.inputType = InputType.TYPE_NULL
            etFromLocation.setOnClickListener(::fromLocation)

            etToLocation.setOnFocusChangeListener { _, b ->
                if (b) {
                    toLocation()
                }
            }
            etToLocation.inputType = InputType.TYPE_NULL
            etToLocation.setOnClickListener(::toLocation)

            btnGetRoute.setOnClickListener {
                originMarker?.remove()
                destinationMarker?.remove()
                searchPolyline?.remove()
                for ((_, line) in routePolyline) {
                    line?.remove()
                }
                lifecycleScope.launch { viewModel.validate(Save.GET_ROUTE) }
            }

            imgClose.setOnClickListener {
                if (viewModel.polylineOptionsList.value == null) {
                    viewModel.changeMapState(MapState.Close)
                } else {
                    viewModel.changeMapState(MapState.ResultInit)
                }
            }

            imgResultClose.setOnClickListener {
                if (viewModel.polylineOptionsList.value == null) {
                    viewModel.changeMapState(MapState.Close)
                } else {
                    viewModel.changeMapState(MapState.ResultInit)
                }
            }


            txtRoute.setOnClickListener {

                val list = viewModel.routeList.map { "$it Km" }.toTypedArray()

                val dialog = DialogHelpers.selectableDialog(
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

                val dialog = DialogHelpers.selectableDialog(
                    context = requireContext(),
                    list = list.toTypedArray(),
                    checkedItem = viewModel.selectTruckId,
                    title = "Select Truck",
                    onSelect = { dialogInterface, id ->
                        viewModel.changeSelectedTruck(id)
                        dialogInterface.dismiss()
                        spinTruck.clearFocus()
                        inputTxtTruck.clearFocus()
                    }
                )
                dialog.show()
            }

        }
    }


    private fun init() {
        bind.txtRoute.also {
            it.keyListener = null
            it.clearFocus()
        }


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














