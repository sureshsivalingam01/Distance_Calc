package com.mrright.distancecalc.data.firestore.models

import androidx.compose.runtime.mutableStateOf
import com.mrright.distancecalc.models.Truck
import com.mrright.distancecalc.utils.helpers.to2Decimal

data class TruckDto(
    var truckType: String = "",
    var allowancePerKm: Double = 0.00,
) {
	fun toTruck() : Truck {
        return Truck(mutableStateOf(truckType), mutableStateOf(allowancePerKm.to2Decimal())).also {
            it.id = id
        }
    }

	var id : String = ""
	var locations : List<LocationDto> = listOf()
	var ranges : List<RangeDto> = listOf()

}


data class TruckDTO(
    var allowancePerKm: Double = 0.00,
    var truckType: String = "",
) {
    var id: String = ""

    fun toTruck(): Truck {
        return Truck(mutableStateOf(truckType), mutableStateOf(allowancePerKm.to2Decimal())).also {
            it.id = id
        }
    }

}







