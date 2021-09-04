package com.mrright.distancecalc.presentation.haulage_fragment

import androidx.lifecycle.ViewModel
import com.mrright.distancecalc.data.firestore.DbRepository
import com.mrright.distancecalc.presentation.states_and_events.MessageEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class HaulageViewModel @Inject constructor(
	private val dbRepository : DbRepository,
) : ViewModel() {

	private val _msgChannel = Channel<MessageEvent>()
	val msg = _msgChannel.receiveAsFlow()

}