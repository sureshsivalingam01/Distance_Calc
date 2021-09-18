package com.mrright.distancecalc.presentation.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mrright.distancecalc.data.firestore.models.HistoryDto
import com.mrright.distancecalc.databinding.ItemHistoryBinding
import com.mrright.distancecalc.utils.helpers.to2Decimal


class HistoryFireStoreAdapter(
    options: FirestoreRecyclerOptions<HistoryDto>,
    private val onClick: (String) -> Unit = {},
) : FirestoreRecyclerAdapter<
        HistoryDto,
        HistoryFireStoreAdapter.HistoryViewHolder,
        >(options) {

    inner class HistoryViewHolder(val bind: ItemHistoryBinding) :
        RecyclerView.ViewHolder(bind.root) {
        init {
            bind.root.setOnClickListener {
                onClick(this@HistoryFireStoreAdapter.snapshots.getSnapshot(adapterPosition).id)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HistoryViewHolder {
        return HistoryViewHolder(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        )
    }

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
        position: Int,
        historyDto: HistoryDto,
    ) {
        with(holder.bind) {
            txtTruckType.text = "Truck : ${historyDto.truckType}"
            txtTotalKm.text = "Total Km : ${historyDto.totalKm}"
            txtAllowance.text = "Allowance : ${historyDto.allowance?.to2Decimal()}"
            txtRoundUp.text = "RoundUp Allowance : ${historyDto.roundUpAllowance?.to2Decimal()}"
            txtFrom.text = "From Location : ${historyDto.from}"
            txtTo.text = "To Location : ${historyDto.to}"
        }
    }
}