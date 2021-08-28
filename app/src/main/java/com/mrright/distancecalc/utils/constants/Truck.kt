package com.mrright.distancecalc.utils.constants

import androidx.annotation.StringRes
import com.mrright.distancecalc.R

enum class Truck(
	@StringRes val id : Int,
	val allowancePerKm : Double,
) {
	LOWBED(R.string.lowbed, 0.84),
	ONE_TON(R.string.one_ton, 0.40),
	THREE_TON(R.string.three_ton, 0.44),
	TEN_TON(R.string.ten_ton, 0.48),
	HILUX(R.string.hilux, 4.10),
}