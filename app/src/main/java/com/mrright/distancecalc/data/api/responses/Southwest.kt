package com.mrright.distancecalc.data.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Southwest(
    @SerializedName("lat") val lat: Double? = null,
    @SerializedName("lng") val lng: Double? = null
)