package com.mrright.distancecalc.data.api.repositories

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.mrright.distancecalc.data.Resource
import com.mrright.distancecalc.data.api.GoogleMapsService
import com.mrright.distancecalc.data.api.responses.DirectionsDTO
import com.mrright.distancecalc.di.MapsApiKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class MapsRepoImpl @Inject constructor(
	private val service : GoogleMapsService,
	@MapsApiKey private val mapsApiKey : String,
) : MapsRepository {
	val TAG = "Maps"

	/*waypoints="optimize:true|"
	+ LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
	+ "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
	+ BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
	+ WALL_STREET.longitude"*/

	val s = "waypoints=optimize:true|origin.lat,origin.lng||des.lat,des.lng"


	override suspend fun getDirection(
		mode : String,
		origin : LatLng,
		destination : LatLng,
		alternatives : Boolean,
	) : Flow<Resource<DirectionsDTO>> = flow {

		try {

			val result = service.getDirection(
				mode = mode,
				origin = "${origin.latitude},${origin.longitude}",
				destination = "${destination.latitude},${destination.longitude}",
				alternatives = alternatives,
				key = mapsApiKey,
			)

			if (result.isSuccessful && result.body()?.status == "OK") {
				emit(Resource.Success(result.body()!!))
				Log.d(TAG, "getDirection::Success::${result.body()?.routes?.size}")
			}
			else {
				throw Exception("UnKnown Error")
			}
		}
		catch (e : Exception) {
			Log.e(TAG, "getDirection::Failure::${e.message}")
			val s = ""
			emit(Resource.Failure(e))
		}

	}

}


interface MapsRepository {

	suspend fun getDirection(
		mode : String,
		origin : LatLng,
		destination : LatLng,
		alternatives : Boolean,
	) : Flow<Resource<DirectionsDTO>>

}