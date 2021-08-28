package com.mrright.distancecalc.data.api.responses


import androidx.annotation.Keep
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

@Keep
data class OverviewPolyline(
    @SerializedName("points") val points : String? = null,
) {


	fun decodePolyline() : MutableList<LatLng>? {


		return points?.let {
			//latLng list
			val poly : MutableList<LatLng> = ArrayList()

			//index for loop
			var index = 0

			//length of the encoded poly points
			val len = it.length

			var lat = 0
			var lng = 0

			//while index is greater than length
			while (index < len) {
				var b : Int
				var shift = 0
				var result = 0

				//do while for latitude
				do {
					b = it[index++].toInt() - 63
					result = result or (b and 0x1f shl shift)
					shift += 5
				} while (b >= 0x20)

				val dLat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
				lat += dLat


				shift = 0
				result = 0

				//do while for longitude
				do {
					b = it[index++].toInt() - 63
					result = result or (b and 0x1f shl shift)
					shift += 5
				} while (b >= 0x20)

				val dLng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
				lng += dLng

				//latLng
				val p = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)

				poly.add(p)
			}
			poly
		}

	}


}