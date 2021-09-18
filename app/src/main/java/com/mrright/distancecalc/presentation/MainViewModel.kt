package com.mrright.distancecalc.presentation

import androidx.lifecycle.ViewModel
import com.mrright.distancecalc.presentation.states_and_events.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Init)
    val viewState: StateFlow<ViewState> get() = _viewState

    fun changeViewState(state: ViewState) {
        _viewState.value = state
    }

}