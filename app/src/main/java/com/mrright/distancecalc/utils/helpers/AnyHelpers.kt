package com.mrright.distancecalc.utils.helpers

import android.util.Log


fun Any.debugLog(
	msg : String = this::class.java.simpleName,
	tr : Throwable? = null,
) = Log.d(this::class.java.simpleName, msg, tr)

fun Any.errorLog(
	msg : String = this::class.java.simpleName,
	tr : Throwable? = null,
) = Log.e(this::class.java.simpleName, msg, tr)

fun Any.infoLog(
	msg : String = this::class.java.simpleName,
	tr : Throwable? = null,
) = Log.i(this::class.java.simpleName, msg, tr)

fun Any.verboseLog(
	msg : String = this::class.java.simpleName,
	tr : Throwable? = null,
) = Log.v(this::class.java.simpleName, msg, tr)

fun Any.warnLog(
	msg : String = this::class.java.simpleName,
	tr : Throwable? = null,
) = Log.w(this::class.java.simpleName, msg, tr)

fun Any.wtfLog(
	msg : String = this::class.java.simpleName,
	tr : Throwable? = null,
) = Log.wtf(this::class.java.simpleName, msg, tr)



