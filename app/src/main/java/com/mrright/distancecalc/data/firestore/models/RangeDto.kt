package com.mrright.distancecalc.data.firestore.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mrright.distancecalc.models.Range
import com.mrright.distancecalc.utils.helpers.to2Decimal

data class RangeDto(
    var fromRange: Int = 0,
    var toRange: Int = 0,
    var allowance: Double = 0.00,
    var additional: Double = 0.00,
) {
	var id : String = ""

	fun toRange() : Range {
		return Range(
            fromRange = mutableStateOf(fromRange.toString()),
            toRange = mutableStateOf(toRange.toString()),
            allowance = mutableStateOf(allowance.to2Decimal()),
            additional = mutableStateOf(additional.to2Decimal()),
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