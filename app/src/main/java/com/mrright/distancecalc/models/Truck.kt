package com.mrright.distancecalc.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.mrright.distancecalc.data.firestore.models.TruckDto
import com.mrright.distancecalc.utils.helpers.checkEmptyToDouble

data class Truck(
	var truckType : MutableState<String> = mutableStateOf(""),
	var allowancePerKm : MutableState<String> = mutableStateOf(""),
) {

	var id : String = ""

	fun toTruckDto() : TruckDto {
		return TruckDto(truckType.value, allowancePerKm.value.checkEmptyToDouble()).also {
			it.id = id
		}
	}

}
