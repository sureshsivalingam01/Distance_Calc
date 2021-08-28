package com.mrright.distancecalc.data.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class DirectionsDTO(
    @SerializedName("geocoded_waypoints") val geocodedWaypoints: List<GeocodedWaypoint>? = null,
    @SerializedName("routes") val routes: List<Route>? = null,
    @SerializedName("status") val status: String? = null
)