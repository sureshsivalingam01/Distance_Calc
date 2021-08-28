package com.mrright.distancecalc.data.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Route(
    @SerializedName("bounds") val bounds: Bounds? = null,
    @SerializedName("copyrights") val copyrights: String? = null,
    @SerializedName("legs") val legs: List<Leg>? = null,
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline? = null,
    @SerializedName("summary") val summary: String? = null,
    @SerializedName("warnings") val warnings: List<Any>? = null,
    @SerializedName("waypoint_order") val waypointOrder: List<Any>? = null
)