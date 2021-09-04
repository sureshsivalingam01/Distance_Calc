package com.mrright.distancecalc.presentation.states_and_events

sealed class ViewState {
	object Init : ViewState()
	object None : ViewState()
}
