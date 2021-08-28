package com.mrright.distancecalc.data.api.responses


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class Distance(
    @SerializedName("text") val text: String? = null,
    @SerializedName("value") val value: Int? = null
)