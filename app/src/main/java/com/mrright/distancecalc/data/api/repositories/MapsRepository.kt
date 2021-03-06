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
    private val service: GoogleMapsService,
    @MapsApiKey private val mapsApiKey: String,
) : MapsRepository {

    companion object {
        private val TAG: String = MapsRepoImpl::class.java.simpleName
    }


    override suspend fun getDirection(
        mode: String,
        origin: LatLng,
        destination: LatLng,
        alternatives: Boolean,
    ): Flow<Resource<DirectionsDTO>> = flow {


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
                Log.d(TAG, "getDirection::Success::${result.body()}")
            } else {
                throw Exception("UnKnown Error")
            }

        } catch (e: Exception) {
            Log.e(TAG, "getDirection::Failure::${e.message}")
            emit(Resource.Failure(e))
        }

    }

}


interface MapsRepository {

    suspend fun getDirection(
        mode: String,
        origin: LatLng,
        destination: LatLng,
        alternatives: Boolean,
    ): Flow<Resource<DirectionsDTO>>

}