package com.mrright.distancecalc.utils.constants

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.mrright.distancecalc.R
import com.mrright.distancecalc.presentation.haulage_fragment.area.AreaFragment
import com.mrright.distancecalc.presentation.haulage_fragment.faf.FafFragment
import com.mrright.distancecalc.presentation.haulage_fragment.form.FormFragment
import com.mrright.distancecalc.presentation.haulage_fragment.gate_charge.GateChargeFragment
import com.mrright.distancecalc.presentation.haulage_fragment.location.LocationFragment

enum class HaulageTabs(
    @StringRes
    val id: Int, val fragment: Fragment
) {
    AREA(R.string.area, AreaFragment()),
    LOCATION(R.string.location, LocationFragment()),
    FAF(R.string.faf, FafFragment()),
    GATE_CHARGE(R.string.gate_charge, GateChargeFragment()),
    FORM(R.string.form, FormFragment()),
}