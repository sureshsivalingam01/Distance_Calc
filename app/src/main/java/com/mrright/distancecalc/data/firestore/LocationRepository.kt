package com.mrright.distancecalc.data.firestore

import com.google.firebase.firestore.DocumentReference
import com.mrright.distancecalc.data.Source
import com.mrright.distancecalc.utils.constants.Collection
import com.mrright.distancecalc.utils.helpers.errorLog
import com.mrright.distancecalc.utils.helpers.infoLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class LocationRepoImpl @Inject constructor(
	db : DocumentReference,
) : LocationRepository {

	private val locationRef = db.collection(Collection.LOCATION.value)

	override suspend fun addLocation(
		locationName : String, area : String, haulageRate : Double, tollCharge : Double, faf : Double, gateCharge : Double, total : Double
	) : Flow<Source> = flow {
		try {

			val map = mapOf(
				Pair("area_id", area),
				Pair("locationName", locationName),
				Pair("haulageRate", haulageRate),
				Pair("tollCharge", tollCharge),
				Pair("faf", faf),
				Pair("gateCharge", gateCharge),
				Pair("total", total),
			)

			locationRef.add(map).await()
			infoLog("addLocation :: Success")
			emit(Source.Success)
		} catch (e : Exception) {
			errorLog("addLocation :: Failed", e)
			emit(Source.Failure(e))
		}
	}

}


interface LocationRepository {

	suspend fun addLocation(
		locationName : String,
		area : String,
		haulageRate : Double,
		tollCharge : Double,
		faf : Double,
		gateCharge : Double,
		total : Double,
	) : Flow<Source>

}