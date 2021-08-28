package com.mrright.distancecalc.models

data class Polylines(
	var origin : Details? = null,
	var destination : Details? = null,
	var routes : MutableMap<Int,Route> = mutableMapOf(),
)
