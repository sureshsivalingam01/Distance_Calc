package com.mrright.distancecalc.data.firestore

import com.google.firebase.firestore.DocumentReference
import com.mrright.distancecalc.data.Resource
import com.mrright.distancecalc.data.Source
import com.mrright.distancecalc.di.Database
import com.mrright.distancecalc.models.Db
import com.mrright.distancecalc.utils.helpers.errorLog
import com.mrright.distancecalc.utils.helpers.infoLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DbRepoImpl @Inject constructor(
	@Database
	private val db : DocumentReference,
) : DbRepository {

	override suspend fun setGateCharge(gateCharge : Double) : Flow<Source> = flow {
		try {
			db.update("gateCharge", gateCharge).await()
			emit(Source.Success)
			infoLog("setGateCharge :: Success")
		} catch (e : Exception) {
			emit(Source.Failure(e))
			errorLog("setGateCharge :: Failed", e)
		}
	}

	override suspend fun setFafPercentage(fafPercentage : Double) : Flow<Source> = flow {
		try {
			db.update("fafPercentage", fafPercentage).await()
			emit(Source.Success)
			infoLog("setFafPercentage :: Success")
		} catch (e : Exception) {
			emit(Source.Failure(e))
			errorLog("setFafPercentage :: Failed", e)
		}
	}

	@ExperimentalCoroutinesApi
	override suspend fun dbCallBack() : Flow<Resource<Db?>> = callbackFlow {
		val listener = db.addSnapshotListener { value, error ->
			error?.let {
				trySend(Resource.Failure(it))
				cancel(error.message ?: "", error)
				errorLog("dbCallBack :: Failed :: ${error.message}")
				return@addSnapshotListener
			}

			trySend(Resource.Success(value?.toObject(Db::class.java)))
			infoLog("getUserCallBack | Success | $value")
		}
		awaitClose { listener.remove() }
	}.flowOn(Dispatchers.IO)
}


interface DbRepository {

	suspend fun setGateCharge(gateCharge : Double) : Flow<Source>

	suspend fun setFafPercentage(fafPercentage : Double) : Flow<Source>

	suspend fun dbCallBack() : Flow<Resource<Db?>>
}


