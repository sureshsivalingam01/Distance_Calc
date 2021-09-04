package com.mrright.distancecalc.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.mrright.distancecalc.data.firestore.models.LocationDto

data class Location(
	var locationName : MutableState<String> = mutableStateOf(""),
	var allowance : MutableState<String> = mutableStateOf(""),
) {
	var id : String = ""


	fun toLocationDto() : LocationDto {
		return LocationDto(
			locationName.value,
			allowance.value.checkEmptyToDouble(),
		).also {
			it.id = id
		}
	}
}


fun List<Location>.toLocationDto() : List<LocationDto> {
	val list = mutableListOf<LocationDto>()
	this.forEach {
		list.add(it.toLocationDto())
	}
	return list
}
