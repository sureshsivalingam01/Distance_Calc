package com.mrright.distancecalc.models

import com.google.android.gms.maps.model.PolylineOptions
import com.mrright.distancecalc.data.api.responses.Distance

data class Route(
	var id : Int = -1,
	var distance : Distance = Distance(),
	var duration : String = "",
	var polylineOptions : PolylineOptions? = null,
	var isSelected : Boolean = false,
)
