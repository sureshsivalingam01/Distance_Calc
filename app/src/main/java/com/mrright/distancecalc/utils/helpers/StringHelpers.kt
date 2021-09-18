package com.mrright.distancecalc.utils.helpers


fun String.checkEmptyToInt(): Int = if (this == "") 0 else this.toInt()

fun String.checkEmptyToDouble(): Double = if (this == "") 0.00 else this.toDouble()








