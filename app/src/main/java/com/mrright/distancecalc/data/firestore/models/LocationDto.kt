package com.mrright.distancecalc.data.firestore.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mrright.distancecalc.models.Location
import com.mrright.distancecalc.utils.helpers.to2Decimal

data class LocationDto(
    var locationName: String = "",
    var allowance: Double = 0.00,
) {
	var id : String = ""

	fun toLocation() : Location {
		return Location(
            mutableStateOf(locationName),
            mutableStateOf(allowance.to2Decimal()),
        ).also {
			it.id = id
		}
	}

}


fun List<LocationDto>.toLocations() : SnapshotStateList<Location> {
	val list = mutableStateListOf<Location>()
	this.forEach {
		list.add(it.toLocation())
	}
	return list
}