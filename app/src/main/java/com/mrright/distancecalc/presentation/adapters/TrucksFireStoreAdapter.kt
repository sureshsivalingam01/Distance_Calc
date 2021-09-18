package com.mrright.distancecalc.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mrright.distancecalc.R
import com.mrright.distancecalc.data.firestore.models.TruckDTO
import com.mrright.distancecalc.databinding.ItemTruckBinding

class TrucksFireStoreAdapter(
	options : FirestoreRecyclerOptions<TruckDTO>,
	private val onClick : (String) -> Unit = {},
) : FirestoreRecyclerAdapter<
		TruckDTO,
		TrucksFireStoreAdapter.TruckViewHolder,
		>(options) {

	inner class TruckViewHolder(val bind : ItemTruckBinding) : RecyclerView.ViewHolder(bind.root) {
		init {
			bind.root.setOnClickListener {
				onClick(this@TrucksFireStoreAdapter.snapshots.getSnapshot(adapterPosition).id)
			}
		}
	}

	override fun onCreateViewHolder(
		parent : ViewGroup,
		viewType : Int,
	) : TruckViewHolder {
		return TruckViewHolder(
			ItemTruckBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false,
			)
		)
	}

	override fun onBindViewHolder(
		holder : TruckViewHolder,
		position : Int,
		truckDto : TruckDTO,
	) {

        val truck = truckDto.toTruck()

        with(holder.bind) {
            txtTruckType.also {
                it.text =
                    it.context.resources.getString(R.string.truck_type_param, truck.truckType.value)
            }
            txtAllowancePerKm.also {
                it.text = it.context.resources.getString(
                    R.string.allowance_per_km_param,
                    truck.allowancePerKm.value
                )
            }
        }
    }
}
