package com.mrright.distancecalc.utils.helpers

import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide


/**
 * Glide url
 *
 * @param url
 */
fun ImageView.glideUrl(url : String) {
	Glide.with(this.context)
		.load(url)
		.into(this)
}


/**
 * Glide url
 *
 * @param url
 * @param placeholder
 */
fun ImageView.glideUrl(
	url : String,
	@DrawableRes placeholder : Int,
) {
	Glide.with(this.context)
		.load(url)
		.placeholder(placeholder)
		.into(this)
}


/**
 * Glide drawable res
 *
 * @param id
 */
fun ImageView.glideDrawableRes(@DrawableRes id : Int) {
	Glide.with(this.context)
		.load(id)
		.into(this)
}
