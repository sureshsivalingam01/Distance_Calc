package com.mrright.distancecalc.data.firestore.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mrright.distancecalc.models.Location

data class LocationDto(
	var locationName : String = "",
	var allowance : Double = 0.0,
) {
	var id : String = ""

	fun toLocation() : Location {
		return Location(
			mutableStateOf(locationName),
			mutableStateOf(allowance.toString()),
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