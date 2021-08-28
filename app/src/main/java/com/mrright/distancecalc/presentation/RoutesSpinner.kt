package com.mrright.distancecalc.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.mrright.distancecalc.data.api.responses.Distance
import com.mrright.distancecalc.databinding.RouteItemBinding

class RoutesSpinner(
	context : Context,
	private val routeList : List<Distance>,
) : ArrayAdapter<Distance>(context, 0, routeList) {

	override fun getView(
		position : Int,
		convertView : View?,
		parent : ViewGroup,
	) : View {
		return initView(position, convertView, parent)
	}


	override fun getDropDownView(
		position : Int,
		convertView : View?,
		parent : ViewGroup,
	) : View {
		return initView(position, convertView, parent)
	}

	private fun initView(
		position : Int,
		convertView : View?,
		parent : ViewGroup,
	) : View {
		val bind = RouteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		bind.txt.text = routeList[position].text
		return bind.root
	}

}