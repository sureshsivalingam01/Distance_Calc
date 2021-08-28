package com.mrright.distancecalc.data.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Step(
    @SerializedName("distance") val distance: Distance? = null,
    @SerializedName("duration") val duration: Duration? = null,
    @SerializedName("end_location") val endLocation: EndLocation? = null,
    @SerializedName("html_instructions") val htmlInstructions: String? = null,
    @SerializedName("polyline") val polyline: Polyline? = null,
    @SerializedName("start_location") val startLocation: StartLocation? = null,
    @SerializedName("travel_mode") val travelMode: String? = null,
    @SerializedName("maneuver") val maneuver: String? = null
)