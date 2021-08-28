package com.mrright.distancecalc.utils.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.mrright.distancecalc.R


//Toast Duration constants
const val SHORT = 0
const val LONG = 1

/**
 * Toast
 *
 * @param text
 * @param duration
 */
@SuppressLint("WrongConstant")
fun Context.toast(
	text : CharSequence = getString(R.string.app_name),
	duration : Int = SHORT,
) {
	Toast.makeText(this, text, duration)
		.show()
}


/**
 * Toast
 *
 * @param id
 * @param duration
 */
@SuppressLint("WrongConstant")
fun Context.toast(
	@StringRes id : Int = R.string.app_name,
	duration : Int = SHORT,
) {
	Toast.makeText(this, id, duration)
		.show()
}