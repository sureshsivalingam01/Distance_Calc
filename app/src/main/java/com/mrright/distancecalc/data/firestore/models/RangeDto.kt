package com.mrright.distancecalc.data.firestore.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mrright.distancecalc.models.Range

data class RangeDto(
	var fromRange : Double = 0.0,
	var toRange : Double = 0.0,
	var allowance : Double = 0.0,
	var additional : Double = 0.0,
) {
	var id : String = ""

	fun toRange() : Range {
		return Range(
			fromRange = mutableStateOf(fromRange.toString()),
			toRange = mutableStateOf(toRange.toString()),
			allowance = mutableStateOf(allowance.toString()),
			additional = mutableStateOf(additional.toString()),
		).also {
			it.id = id
		}
	}

}

fun List<RangeDto>.toRanges() : SnapshotStateList<Range> {
	val list = mutableStateListOf<Range>()
	this.forEach {
		list.add(it.toRange())
	}
	return list
}