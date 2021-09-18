package com.mrright.distancecalc.data.firestore.models

data class CountryBounds(
    var countryName: String? = "",
    var bound: Bounds? = null,
)


data class Bounds(
    var northeast: LatLong? = null,
    var southwest: LatLong? = null,
)

data class LatLong(
    var latitude: Double = 0.00,
    var longitude: Double = 0.00,
)