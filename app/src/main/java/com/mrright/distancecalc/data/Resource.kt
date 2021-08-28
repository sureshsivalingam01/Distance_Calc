package com.mrright.distancecalc.data

sealed class Resource<out T> {
	data class Success<out R>(val value : R) : Resource<R>()
	data class Failure(val ex : Throwable) : Resource<Nothing>()
}

sealed class Source {
	object Success : Source()
	data class Failure(val ex : Throwable) : Source()
}