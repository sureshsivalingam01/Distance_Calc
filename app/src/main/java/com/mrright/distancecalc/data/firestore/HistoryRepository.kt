package com.mrright.distancecalc.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.mrright.distancecalc.data.Resource
import com.mrright.distancecalc.data.Source
import com.mrright.distancecalc.data.firestore.models.Bounds
import com.mrright.distancecalc.data.firestore.models.CountryBounds
import com.mrright.distancecalc.di.Country
import com.mrright.distancecalc.di.History
import com.mrright.distancecalc.utils.helpers.errorLog
import com.mrright.distancecalc.utils.helpers.infoLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class HistoryRepoImpl @Inject constructor(
    @History
    private val history: CollectionReference,
    @Country
    private val country: CollectionReference,
) : HistoryRepository {


    override suspend fun addSearch(
        truckType: String,
        from: String,
        to: String,
        totalKm: Int,
        allowance: Double,
        roundUpAllowance: Double,
    ): Flow<Source> = flow {
        try {

            val map =
                mapOf(
                    Pair("truckType", truckType),
                    Pair("from", from),
                    Pair("to", to),
                    Pair("totalKm", totalKm),
                    Pair("allowance", allowance),
                    Pair("roundUpAllowance", roundUpAllowance),
                    Pair("timestamp", Timestamp.now()),
                )

            history.add(map)
                .await()
            infoLog("addSearch :: Success")
            emit(Source.Success)
        } catch (e: Exception) {
            errorLog("addSearch :: Failed", e)
            emit(Source.Failure(e))
        }
    }


    override suspend fun saveBounds(
        countryName: String,
        bounds: Bounds,
    ): Flow<Source> = flow {
        try {
            val countryBounds = CountryBounds(countryName, bounds)
            country.add(countryBounds)
                .await()
            infoLog("saveBounds :: Success")
            emit(Source.Success)
        } catch (e: Exception) {
            errorLog("saveBounds :: Failed", e)
            emit(Source.Failure(e))
        }
    }


    override suspend fun getBound(
        countryName: String,
    ): Flow<Resource<CountryBounds>> = flow {
        try {
            val result =
                country.whereEqualTo("countryName", countryName)
                    .get()
                    .await()


            val country = result.documents[0].toObject(CountryBounds::class.java)!!
            infoLog("getBound :: Success :: $country")
            emit(Resource.Success(country))
        } catch (e: Exception) {
            errorLog("getBound :: Failed", e)
            emit(Resource.Failure(e))
        }
    }

}


interface HistoryRepository {

    suspend fun addSearch(
        truckType: String,
        from: String,
        to: String,
        totalKm: Int,
        allowance: Double,
        roundUpAllowance: Double,
    ): Flow<Source>

    suspend fun saveBounds(
        countryName: String,
        bounds: Bounds,
    ): Flow<Source>


    suspend fun getBound(
        countryName: String,
    ): Flow<Resource<CountryBounds>>

}