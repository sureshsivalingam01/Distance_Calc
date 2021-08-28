package com.mrright.distancecalc.presentation.mapFragment

import com.mrright.distancecalc.utils.constants.Truck


sealed class SearchViewState {

	object Visible : SearchViewState()
	object Gone : SearchViewState()
	object None : SearchViewState()

}

sealed class MapState {
	object Init : MapState()
	object Search : MapState()
	object ChangeRoute : MapState()
	object SearchResult : MapState()
	//object SearchError : MapState()
}