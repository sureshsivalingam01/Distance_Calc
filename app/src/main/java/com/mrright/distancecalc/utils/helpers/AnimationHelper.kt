package com.mrright.distancecalc.utils.helpers

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes


fun View.startAnimating(
	@AnimRes id : Int,
	duration : Long,
) : Animation {
	return AnimationUtils.loadAnimation(this.context, id)
		.apply {
			this.duration = duration
			this@startAnimating.startAnimation(this)
		}
}