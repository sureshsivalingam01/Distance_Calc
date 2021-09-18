package com.mrright.distancecalc.presentation.dashboard.map


sealed class MapState {
    object Init : MapState()
    object Search : MapState()
    object ChangeRoute : MapState()
    object Result : MapState()
    object ResultInit : MapState()
    object FabResult : MapState()
    object FabSearch : MapState()
    object Close : MapState()
}