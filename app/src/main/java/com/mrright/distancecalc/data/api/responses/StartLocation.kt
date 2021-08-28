package com.mrright.distancecalc.data.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class StartLocation(
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("lng") val lng: Double? = null
)