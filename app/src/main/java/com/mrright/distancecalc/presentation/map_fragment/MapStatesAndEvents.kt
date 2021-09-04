package com.mrright.distancecalc.presentation.map_fragment


sealed class MapState {
	object Init : MapState()
	object Search : MapState()
	object ChangeRoute : MapState()
	object SearchResult : MapState()
	//object SearchError : MapState()
}