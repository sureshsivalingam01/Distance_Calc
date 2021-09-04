package com.mrright.distancecalc.utils.helpers

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 * Open activity
 *
 * @param T
 * @param activity
 * @param extras
 * @receiver
 */
inline fun <T> Activity.openActivity(
	activity : Class<T>,
	extras : Bundle.() -> Unit = {},
) {
	Intent(applicationContext, activity).apply {
		putExtras(Bundle().apply(extras))
		startActivity(this)
	}
}




















