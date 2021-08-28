package com.mrright.distancecalc.data.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Bounds(
    @SerializedName("northeast") val northeast: Northeast? = null,
    @SerializedName("southwest") val southwest: Southwest? = null
)