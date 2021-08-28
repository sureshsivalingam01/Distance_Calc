package com.mrright.distancecalc.presentation

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter

object SimpleSpinner {

	//SIMPLE SPINNER ARRAY returns the SpinnerAdapter
	fun init(
		context : Context,
		list : Array<String>,
	) : SpinnerAdapter {
		return ArrayAdapter(context, android.R.layout.simple_spinner_item, list).apply {
			setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		}
	}

}