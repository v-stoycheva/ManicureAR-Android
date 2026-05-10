package com.viktoriastoycheva.manicurear.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.viktoriastoycheva.manicurear.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeSlotAdapter(private val onTimeSelected: (LocalDateTime) -> Unit) :
    RecyclerView.Adapter<TimeSlotAdapter.TimeViewHolder>() {

    private var slots = listOf<LocalDateTime>()
    private var selectedPosition = -1

    fun submitList(newList: List<LocalDateTime>) {
        slots = newList
        selectedPosition = -1 // Нулираме избора при нов ден
        notifyDataSetChanged()
    }

    class TimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btn: MaterialButton = view.findViewById(R.id.btnTimeSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time_slot, parent, false)
        return TimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val time = slots[position]
        holder.btn.text = time.format(DateTimeFormatter.ofPattern("HH:mm"))

        // Визуална индикация за избор
        if (selectedPosition == position) {
            holder.btn.setBackgroundColor(Color.parseColor("#D4AF37")) // Gold
            holder.btn.setTextColor(Color.WHITE)
        } else {
            holder.btn.setBackgroundColor(Color.TRANSPARENT)
            holder.btn.setTextColor(Color.BLACK)
            holder.btn.strokeColor = android.content.res.ColorStateList.valueOf(Color.parseColor("#F0F0F0"))
        }

        holder.btn.setOnClickListener {
            val previous = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previous)
            notifyItemChanged(selectedPosition)
            onTimeSelected(time)
        }
    }

    override fun getItemCount() = slots.size
}