package com.mrright.distancecalc.data.firestore

import com.google.firebase.firestore.CollectionReference
import com.mrright.distancecalc.data.Source
import com.mrright.distancecalc.di.Area
import com.mrright.distancecalc.utils.helpers.errorLog
import com.mrright.distancecalc.utils.helpers.infoLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AreaRepoImpl @Inject constructor(
	@Area private val area: CollectionReference,
) : AreaRepository {

	override suspend fun addArea(areaName : String) : Flow<Source> = flow {
		try {
			val map = mapOf(Pair("areaName", areaName))
			area.add(map).await()
			infoLog("addArea :: Success")
			emit(Source.Success)
		} catch (e : Exception) {
			errorLog("addArea :: Failed", e)
			emit(Source.Failure(e))
		}
	}

}


interface AreaRepository {

	suspend fun addArea(areaName : String) : Flow<Source>

}