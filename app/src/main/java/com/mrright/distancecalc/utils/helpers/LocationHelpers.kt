package com.mrright.distancecalc.utils.helpers

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place

val locationSettingsRequest : LocationSettingsRequest = LocationRequest.create()
	.apply {
		priority = LocationRequest.PRIORITY_HIGH_ACCURACY
		interval = 6000L
		fastestInterval = 6000L
	}
	.run {
		LocationSettingsRequest.Builder()
			.apply {
				addLocationRequest(this@run)
				setAlwaysShow(true)
			}
			.build()
	}


val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

fun Context.isGpsTurnedOn() : Boolean {
	val lM = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
	return lM.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

 fun decodePolyline(encoded: String): List<LatLng> {
	//latLng list
	val poly: MutableList<LatLng> = ArrayList()

	//index for loop
	var index = 0

	//length of the encoded poly points
	val len = encoded.length

	var lat = 0
	var lng = 0

	//while index is greater than length
	while (index<len) {
		var b : Int
		var shift = 0
		var result = 0

		//do while for latitude
		do {
			b = encoded[index++].code - 63
			result = result or (b and 0x1f shl shift)
			shift += 5
		} while (b>=0x20)

		val dLat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
		lat += dLat


		shift = 0
		result = 0

		//do while for longitude
		do {
			b = encoded[index++].code - 63
			result = result or (b and 0x1f shl shift)
			shift += 5
		} while (b>=0x20)

		val dLng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
		lng += dLng

		//latLng
		val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)

		poly.add(p)
	}
	return poly
}