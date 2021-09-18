package com.mrright.distancecalc.utils.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

//Toast Duration constants
const val SHORT = 0
const val LONG = 1


/**
 * Open activity
 *
 * @param T
 * @param activity
 * @param extras
 * @receiver
 */
inline fun <T> Activity.openActivity(
    activity: Class<T>,
    extras: Bundle.() -> Unit = {},
) {
    Intent(applicationContext, activity).apply {
        putExtras(Bundle().apply(extras))
        startActivity(this)
    }
}

inline fun <T> Fragment.openActivity(
    activity: Class<T>,
    extras: Bundle.() -> Unit = {},
) {
    Intent(requireContext(), activity).apply {
        putExtras(Bundle().apply(extras))
        startActivity(this)
    }
}


@SuppressLint("WrongConstant")
fun Activity.toast(
    text: CharSequence = "",
    duration: Int = SHORT,
) {
    Toast.makeText(applicationContext, text, duration)
        .show()
}


@SuppressLint("WrongConstant")
fun Fragment.toast(
    text: CharSequence = "",
    duration: Int = SHORT,
) {
    Toast.makeText(requireContext(), text, duration)
        .show()
}


@SuppressLint("WrongConstant")
fun Activity.toast(
    @StringRes
    id: Int,
    duration: Int = SHORT,
) {
    Toast.makeText(applicationContext, id, duration)
        .show()
}


@SuppressLint("WrongConstant")
fun Fragment.toast(
    @StringRes
    id: Int,
    duration: Int = SHORT,
) {
    Toast.makeText(requireContext(), id, duration)
        .show()
}







