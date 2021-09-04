package com.mrright.distancecalc.presentation.adapters

import android.graphics.Typeface
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.mrright.distancecalc.databinding.PlaceItemBinding

class PlacesAdapter(
	private val listPrediction : List<AutocompletePrediction>,
	private val onClick : (String, String) -> Unit = { s : String, s1 : String -> },
) : RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder>() {

	override fun getItemCount() = listPrediction.size

	override fun onCreateViewHolder(
		parent : ViewGroup,
		viewType : Int,
	) : PlacesViewHolder {
		val bind = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return PlacesViewHolder(bind)
	}

	inner class PlacesViewHolder(val bind : PlaceItemBinding) : RecyclerView.ViewHolder(bind.root) {

		init {
			bind.root.setOnClickListener {
				onClick(listPrediction[adapterPosition].placeId, bind.secondTxt.text.toString())
			}
		}

	}

	override fun onBindViewHolder(
		holder : PlacesViewHolder,
		position : Int,
	) {
		val style = StyleSpan(Typeface.BOLD)
		holder.bind.primaryTxt.text = listPrediction[position].getPrimaryText(style)
		holder.bind.secondTxt.text = listPrediction[position].getFullText(null)
	}


}