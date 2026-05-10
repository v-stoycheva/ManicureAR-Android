package com.viktoriastoycheva.manicurear.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.viktoriastoycheva.manicurear.R
import com.viktoriastoycheva.manicurear.models.Appointment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onCancelClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    fun updateList(newList: List<Appointment>) {
        appointments = newList
        notifyDataSetChanged()
    }

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvServiceName: TextView = view.findViewById(R.id.tvServiceName)
        val tvArtistName: TextView = view.findViewById(R.id.tvArtistName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val btnCancel: MaterialButton = view.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        val now = LocalDateTime.now()
        val limitForCancel = appointment.startTime.minusHours(24)
        val isPast = appointment.startTime.isBefore(now)

        holder.tvServiceName.text = appointment.service.title
        holder.tvArtistName.text = "Artist: ${appointment.manicurist.firstName} ${appointment.manicurist.lastName}"
        holder.tvStatus.text = appointment.status

        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' HH:mm", Locale.ENGLISH)
        holder.tvDateTime.text = appointment.startTime.format(formatter)

        if (isPast || appointment.status == "CANCELLED" || appointment.status == "COMPLETED" || appointment.status == "NOSHOW") {
            holder.itemView.alpha = 0.6f
            holder.btnCancel.visibility = View.GONE

            if (appointment.status == "COMPLETED") {
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50")) // Зелено за завършено
            } else {
                holder.tvStatus.setTextColor(android.graphics.Color.GRAY)
            }
        }
        else if (now.isAfter(limitForCancel)) {
            holder.btnCancel.visibility = View.GONE
        }
        else {
            holder.itemView.alpha = 1.0f
            holder.btnCancel.visibility = View.VISIBLE
            holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.gold))
            holder.btnCancel.setOnClickListener { onCancelClick(appointment) }
        }

        // Промяна на цвета на статуса
        when (appointment.status) {
            "CANCELLED" -> holder.tvStatus.setTextColor(android.graphics.Color.GRAY)
            "BOOKED" -> holder.tvStatus.setTextColor(holder.itemView.context.getColor(R.color.gold))
            else -> holder.tvStatus.setTextColor(android.graphics.Color.BLACK)
        }
    }

    override fun getItemCount() = appointments.size
}