package com.mrright.distancecalc.data.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Polyline(
    @SerializedName("points") val points: String? = null
)