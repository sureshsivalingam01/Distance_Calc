package com.mrright.distancecalc.data.firestore.models

import com.google.firebase.Timestamp

data class HistoryDto(
    var truckType: String? = "",
    var from: String? = "",
    var to: String? = "",
    var totalKm: Int? = 0,
    var allowance: Double? = 0.00,
    var roundUpAllowance: Double? = 0.00,
    var timestamp: Timestamp? = null,
) {
    var id: String? = ""
}
