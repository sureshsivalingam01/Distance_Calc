package com.mrright.distancecalc.utils.helpers


fun Double.to2Decimal(): String {

    return String.format("%.2f", this)

    //return this.toBigDecimal().setScale(2,RoundingMode.CEILING).toDouble()

    /*return NumberFormat.getInstance().apply {
		maximumFractionDigits = 2
	}.let {
		it.format(this).toDouble()
	}*/

    //return DecimalFormat("#.00").format(this).toFloat()

}