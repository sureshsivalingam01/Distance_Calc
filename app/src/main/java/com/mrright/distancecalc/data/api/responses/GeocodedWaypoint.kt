package com.mrright.distancecalc.data.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class GeocodedWaypoint(
    @SerializedName("geocoder_status") val geocoderStatus: String? = null,
    @SerializedName("place_id") val placeId: String? = null,
    @SerializedName("types") val types: List<String>? = null
)