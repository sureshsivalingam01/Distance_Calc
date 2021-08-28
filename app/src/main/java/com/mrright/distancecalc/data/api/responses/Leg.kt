package com.mrright.distancecalc.data.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Leg(
    @SerializedName("distance") val distance: Distance? = null,
    @SerializedName("duration") val duration: Duration? = null,
    @SerializedName("end_address") val endAddress: String? = null,
    @SerializedName("end_location") val endLocation: EndLocation? = null,
    @SerializedName("start_address") val startAddress: String? = null,
    @SerializedName("start_location") val startLocation: StartLocation? = null,
    @SerializedName("steps") val steps: List<Step>? = null,
    @SerializedName("traffic_speed_entry") val trafficSpeedEntry: List<Any>? = null,
    @SerializedName("via_waypoint") val viaWaypoint: List<Any>? = null
)