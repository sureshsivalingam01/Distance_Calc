package com.mrright.distancecalc.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.mrright.distancecalc.data.firestore.models.RangeDto

data class Range(
	var fromRange : MutableState<String> = mutableStateOf(""),
	var toRange : MutableState<String> = mutableStateOf(""),
	var allowance : MutableState<String> = mutableStateOf(""),
	var additional : MutableState<String> = mutableStateOf(""),
) {
	var id : String = ""

	fun toRangeDto() : RangeDto {
		return RangeDto(
			fromRange.value.checkEmptyToDouble(),
			toRange.value.checkEmptyToDouble(),
			allowance.value.checkEmptyToDouble(),
			additional.value.checkEmptyToDouble(),
		).also {
			it.id = id
		}
	}
}


fun List<Range>.toRangesDto() : List<RangeDto> {
	val list = mutableListOf<RangeDto>()
	this.forEach {
		list.add(it.toRangeDto())
	}
	return list
}


fun String.checkEmptyToDouble() : Double = if (this == "") 0.0 else this.toDouble()
