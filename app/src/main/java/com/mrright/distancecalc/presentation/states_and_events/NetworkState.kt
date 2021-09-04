package com.mrright.distancecalc.presentation.states_and_events

sealed class NetworkState<out T> {
	data class Success<out R>(val value : R) : NetworkState<R>()
	data class Error(val msg : String = "Unknown Error") : NetworkState<Nothing>()
	data class Loading(val msg : String = "Loading") : NetworkState<Nothing>()
	object None : NetworkState<Nothing>()
}