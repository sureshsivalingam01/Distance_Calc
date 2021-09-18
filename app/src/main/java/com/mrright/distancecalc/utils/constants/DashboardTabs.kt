package com.mrright.distancecalc.utils.constants

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.mrright.distancecalc.R
import com.mrright.distancecalc.presentation.dashboard.map.MapsFragment
import com.mrright.distancecalc.presentation.history_fragment.HistoryFragment

enum class DashboardTabs(
    @StringRes
    val id: Int, val fragment: Fragment
) {
    MAP(R.string.map, MapsFragment()),
    HISTORY(R.string.history, HistoryFragment()),
}