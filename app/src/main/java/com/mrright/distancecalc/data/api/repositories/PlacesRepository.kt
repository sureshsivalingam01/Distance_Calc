package com.mrright.distancecalc.data.api.repositories

import android.util.Log
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.ktx.api.net.awaitFetchPlace
import com.google.android.libraries.places.ktx.api.net.awaitFindAutocompletePredictions
import com.mrright.distancecalc.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject



class PlacesRepoImpl @Inject constructor(
	private val placesClient : PlacesClient,
) : PlacesRepository {

	val TAG = "Places"

	override suspend fun placesAutoCompletePredicts(
		predictionsRequest : FindAutocompletePredictionsRequest,
	) : Resource<MutableList<AutocompletePrediction>> {

		return try {
			val result = placesClient.awaitFindAutocompletePredictions(predictionsRequest)
			Log.d(TAG, "placesAutoCompletePredicts::Success::${result.autocompletePredictions.size}")
			Resource.Success(result.autocompletePredictions)
		}
		catch (e : Exception) {
			Resource.Failure(e)
		}


	}

	override suspend fun fetchPlace(fetchPlaceRequest : FetchPlaceRequest) : Flow<Resource<Place>> = flow {
		try {
			val result = placesClient.awaitFetchPlace(fetchPlaceRequest)
			Log.d(TAG, "fetchPlace::Success::${result.place.latLng}")
			emit(Resource.Success(result.place))
		}
		catch (e : Exception) {
			emit(Resource.Failure(e))
		}
	}

}


interface PlacesRepository {

	suspend fun placesAutoCompletePredicts(
		predictionsRequest : FindAutocompletePredictionsRequest,
	) : Resource<MutableList<AutocompletePrediction>>

	suspend fun fetchPlace(fetchPlaceRequest : FetchPlaceRequest) : Flow<Resource<Place>>

}