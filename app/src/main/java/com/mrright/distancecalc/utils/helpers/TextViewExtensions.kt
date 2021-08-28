package com.mrright.distancecalc.utils.helpers

import android.widget.TextView
import androidx.annotation.StringRes

/**
 * Set string res
 *
 * @param id
 */
fun TextView.setStringRes(@StringRes id : Int) {
	this.text = this.context.resources.getString(id)
}