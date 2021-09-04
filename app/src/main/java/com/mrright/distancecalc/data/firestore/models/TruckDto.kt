package com.mrright.distancecalc.data.firestore.models

import androidx.compose.runtime.mutableStateOf
import com.mrright.distancecalc.models.Truck

data class TruckDto(
	var truckType : String = "",
	var allowancePerKm : Double = 0.0,
) {
	fun toTruck() : Truck {
		return Truck(mutableStateOf(truckType), mutableStateOf(allowancePerKm.toString())).also {
			it.id = id
		}
	}

	var id : String = ""
	var locations : List<LocationDto> = listOf()
	var ranges : List<RangeDto> = listOf()

}


data class TruckDTO(
	var allowancePerKm : Any = "",
	var truckType : String = "",
) {
	var id : String = ""
}







