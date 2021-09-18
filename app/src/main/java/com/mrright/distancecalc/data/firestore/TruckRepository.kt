package com.mrright.distancecalc.data.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.mrright.distancecalc.data.Resource
import com.mrright.distancecalc.data.Source
import com.mrright.distancecalc.data.firestore.models.LocationDto
import com.mrright.distancecalc.data.firestore.models.RangeDto
import com.mrright.distancecalc.data.firestore.models.TruckDto
import com.mrright.distancecalc.di.Truck
import com.mrright.distancecalc.utils.constants.Collection
import com.mrright.distancecalc.utils.helpers.errorLog
import com.mrright.distancecalc.utils.helpers.infoLog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class TruckRepoImpl @Inject constructor(
    @Truck private val truck: CollectionReference,
) : TruckRepository {


	override suspend fun deleteRange(truckId : String, rangeId : String) : Flow<Source> = flow {

		try {
            truck.document(truckId).collection(Collection.RANGE.value).document(rangeId).delete()
                .await()
            infoLog("deleteRange :: Success :: Deleted")
            emit(Source.Success)
        } catch (e : Exception) {
			errorLog("deleteRange :: Failed", e)
			emit(Source.Failure(e))
		}


	}


	override suspend fun deleteLocation(truckId : String, locationId : String) : Flow<Source> = flow {
		try {
            truck.document(truckId).collection(Collection.LOCATION.value).document(locationId)
                .delete().await()
            infoLog("deleteLocation :: Success :: Deleted")
            emit(Source.Success)
        } catch (e : Exception) {
			errorLog("deleteLocation :: Failed", e)
			emit(Source.Failure(e))
		}
	}


	override suspend fun deleteTruck(truckId : String) : Flow<Source> = flow {
		try {
            truck.document(truckId).delete().await()
            infoLog("deleteTruck :: Success :: Deleted")
            emit(Source.Success)
        } catch (e : Exception) {
			errorLog("deleteTruck :: Failed", e)
			emit(Source.Failure(e))
		}
	}


	override suspend fun getTruck(truckId : String) : Flow<Resource<TruckDto>> = flow {

		try {

            val ranges: MutableList<RangeDto> = mutableListOf()
            val locations: MutableList<LocationDto> = mutableListOf()
            var truckDto = TruckDto()

            truck.document(truckId).get().await()?.let { snapshot ->
                snapshot.toObject(TruckDto::class.java)?.let {
                    it.id = snapshot.id
                    truckDto = it
                }
            }

            truck.document(truckId).collection(Collection.RANGE.value).get().await()
                ?.let { snapshot ->
                    snapshot.forEachIndexed { i, it ->
                        ranges.add(it.toObject(RangeDto::class.java))
                        ranges[i].id = it.id
                    }
                }

            truck.document(truckId).collection(Collection.LOCATION.value).get().await()
                ?.let { snapshot ->
                    snapshot.forEachIndexed { i, it ->
                        locations.add(it.toObject(LocationDto::class.java))
                        locations[i].id = it.id
                    }
                }

            truckDto.also {
                it.locations = locations
                it.ranges = ranges
            }

            infoLog("getTruck :: Success :: $truckDto")
            emit(Resource.Success(truckDto))

        } catch (e: Exception) {
            errorLog("getTruck :: Failed", e)
            emit(Resource.Failure(e))
        }
    }


    override suspend fun addTruck(
        truckDto: TruckDto,
        ranges: List<RangeDto>,
        locations: List<LocationDto>
    ): Flow<Source> = flow {

        try {

            val truckMap = mapOf(
                Pair("truckType", truckDto.truckType),
                Pair("allowancePerKm", truckDto.allowancePerKm)
            )

            val reference = truck.add(truckMap).await()

            ranges.forEach {

                val map = mapOf(
                    Pair("fromRange", it.fromRange),
                    Pair("toRange", it.toRange),
                    Pair("allowance", it.allowance),
                    Pair("additional", it.additional),
                )

				reference.collection(Collection.RANGE.value).add(map).await()
			}

			locations.forEach {

				val map = mapOf(
					Pair("locationName", it.locationName),
					Pair("allowance", it.allowance),
				)
				reference.collection(Collection.LOCATION.value).add(map).await()
			}

            infoLog("addTruck :: Success")
            emit(Source.Success)

        } catch (e: Exception) {
            errorLog("addTruck :: Failed", e)
            emit(Source.Failure(e))
        }
    }


    override suspend fun updateTruck(
        truckDto: TruckDto,
        ranges: List<RangeDto>,
        locations: List<LocationDto>
    ): Flow<Source> = flow {
        try {

            val truckMap = mapOf(
                Pair("truckType", truckDto.truckType),
                Pair("allowancePerKm", truckDto.allowancePerKm)
            )

            truck.document(truckDto.id).update(truckMap).await()

            locations.forEach {
                val locationMap = mapOf(
                    Pair("locationName", it.locationName), Pair("allowance", it.allowance)
                )

                if (it.id == "") {
                    truck.document(truckDto.id).collection(Collection.LOCATION.value)
                        .add(locationMap).await()
                } else {
                    truck.document(truckDto.id).collection(Collection.LOCATION.value)
                        .document(it.id).update(locationMap).await()
                }
			}

			ranges.forEach {
				val rangeMap = mapOf(
					Pair("fromRange", it.fromRange),
					Pair("toRange", it.toRange),
					Pair("allowance", it.allowance),
					Pair("additional", it.additional),
                )

                if (it.id == "") {
                    truck.document(truckDto.id).collection(Collection.RANGE.value).add(rangeMap)
                        .await()
                } else {
                    truck.document(truckDto.id).collection(Collection.RANGE.value).document(it.id)
                        .update(rangeMap).await()
                }

            }

            infoLog("updateTruck :: Success :: Updated $truckDto")
            emit(Source.Success)

        } catch (e : Exception) {
			errorLog("updateTruck :: Failed", e)
			emit(Source.Failure(e))
		}
	}


	override suspend fun getTrucks() : Flow<Resource<List<TruckDto>>> = flow {
		try {

            truck.get().await().let { snapshot ->
                emit(Resource.Success(snapshot.toObjects(TruckDto::class.java)))
            }

            infoLog("getTrucks :: Success :: ")
        } catch (e : Exception) {
			errorLog("getTrucks :: Failed", e)
			emit(Resource.Failure(e))
		}
	}


	@ExperimentalCoroutinesApi
	override suspend fun getTrucksCallBack() : Flow<Resource<List<TruckDto>>> = callbackFlow {


        val listener = truck.addSnapshotListener { value, error ->
            error?.let {
                trySend(Resource.Failure(it))
                cancel(it.message ?: "")
                errorLog("getTrucksCallBack :: Failed", it)
            }

            val list = mutableListOf<TruckDto>()

            value?.forEach { snapshot ->
                snapshot.toObject(TruckDto::class.java).let { truckDto ->
					list.add(TruckDto(
						truckDto.truckType, truckDto.allowancePerKm
					).also {
						it.id = snapshot.id
					})
				}
			}

			trySend(Resource.Success(list))
			infoLog("getTrucksCallBack :: Success :: $value")

		}
		awaitClose { listener.remove() }

	}


    override suspend fun getRange(
		truckId : String,
		totalKm : Int,
	) : Flow<Resource<RangeDto>> = flow {

		try {

            var range: RangeDto? = null

            val rangeListener = truck.document(truckId).collection(Collection.RANGE.value)
                .whereLessThanOrEqualTo("fromRange", totalKm)
                .orderBy("fromRange", Query.Direction.DESCENDING).get().await()

            rangeListener.documents.forEach { snapshot ->
                snapshot.toObject(RangeDto::class.java)?.let { rangeDto ->

                    if (totalKm >= rangeDto.fromRange && totalKm <= rangeDto.toRange) {


                        range = RangeDto(
                            rangeDto.fromRange,
                            rangeDto.toRange,
                            rangeDto.allowance,
                            rangeDto.additional
						).also {
							it.id = snapshot.id
						}

						infoLog("getRange :: Success :: $range")
					}
				}
			}

			if (range != null) {
				emit(Resource.Success(range!!))
			} else {
				throw Exception("RangeNotFound")
			}


		} catch (e : Exception) {
			errorLog("getRange :: Failed", e)
			emit(Resource.Failure(e))
		}

	}


    override suspend fun getLocation(truckId : String, from : String, to : String) : Flow<Resource<List<LocationDto>>> = flow {

		val locations = mutableListOf<LocationDto>()

		try {
            val locationsSnaps =
                truck.document(truckId).collection(Collection.LOCATION.value)
                    .whereIn("locationName", listOf(from, to)).get().await()

			locationsSnaps.documents.forEach { snapshot ->
				locations.add(snapshot.toObject(LocationDto::class.java)!!.also {
					it.id = snapshot.id
				})
			}

			infoLog("getLocation :: Success :: ${locationsSnaps.toObjects(LocationDto::class.java)}")

			emit(Resource.Success(locations))

		} catch (e : Exception) {
			errorLog("getLocation :: Failed", e)
			emit(Resource.Failure(e))
		}

	}


    override suspend fun getOnlyTruck(truckId : String) : Flow<Resource<TruckDto>> = flow {

		try {

            truck.document(truckId).get().await()?.let { snapshot ->
                snapshot.toObject(TruckDto::class.java)?.let {
                    it.id = snapshot.id
                    infoLog("getOnlyTruck :: Success :: $it")
                    emit(Resource.Success(it))
                }
            }

        } catch (e : Exception) {
            errorLog("getOnlyTruck :: Failed", e)
            emit(Resource.Failure(e))
        }

	}


}


interface TruckRepository {

	suspend fun deleteRange(truckId : String, rangeId : String) : Flow<Source>

    suspend fun deleteLocation(truckId: String, locationId: String): Flow<Source>

    suspend fun deleteTruck(truckId: String): Flow<Source>

    suspend fun getTruck(truckId: String): Flow<Resource<TruckDto>>

    suspend fun getTrucks(): Flow<Resource<List<TruckDto>>>

    suspend fun getTrucksCallBack(): Flow<Resource<List<TruckDto>>>

    suspend fun addTruck(
        truckDto: TruckDto,
        ranges: List<RangeDto>,
        locations: List<LocationDto>
    ): Flow<Source>

    suspend fun updateTruck(
        truckDto: TruckDto,
        ranges: List<RangeDto>,
        locations: List<LocationDto>
    ): Flow<Source>

    suspend fun getRange(truckId: String, totalKm: Int): Flow<Resource<RangeDto>>

    suspend fun getLocation(
        truckId: String,
        from: String,
        to: String
    ): Flow<Resource<List<LocationDto>>>

    suspend fun getOnlyTruck(truckId: String): Flow<Resource<TruckDto>>

}