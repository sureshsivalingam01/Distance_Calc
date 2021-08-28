package com.mrright.distancecalc.models

import com.google.android.gms.maps.model.LatLng

data class Details(
	var placeId : String = "",
	var latLng : LatLng? = null,
	var placeName : String = "",
	var address : String = "",
)
