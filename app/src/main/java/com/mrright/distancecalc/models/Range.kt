package com.mrright.distancecalc.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.mrright.distancecalc.data.firestore.models.RangeDto
import com.mrright.distancecalc.utils.helpers.checkEmptyToDouble
import com.mrright.distancecalc.utils.helpers.checkEmptyToInt

data class Range(
	var fromRange : MutableState<String> = mutableStateOf(""),
	var toRange : MutableState<String> = mutableStateOf(""),
	var allowance : MutableState<String> = mutableStateOf(""),
	var additional : MutableState<String> = mutableStateOf(""),
) {
	var id : String = ""

	fun toRangeDto() : RangeDto {
		return RangeDto(
            fromRange.value.checkEmptyToInt(),
            toRange.value.checkEmptyToInt(),
            allowance.value.checkEmptyToDouble(),
            additional.value.checkEmptyToDouble(),
        ).also {
			it.id = id
		}
	}
}


