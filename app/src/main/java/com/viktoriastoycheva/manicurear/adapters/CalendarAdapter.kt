package com.viktoriastoycheva.manicurear.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.viktoriastoycheva.manicurear.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

class CalendarAdapter(private val onDateSelected: (LocalDateTime) -> Unit) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private val days = mutableListOf<LocalDateTime>()
    private var selectedPosition = -1 // Пази индекса на избрания ден

    init {
        val today = LocalDateTime.now()
        // Намираме понеделника на текущата седмица
        val monday = today.with(java.time.DayOfWeek.MONDAY)
        for (i in 0..90) {
            days.add(monday.plusDays(i.toLong()))
        }
    }

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayName: TextView = view.findViewById(R.id.tvDayName)
        val dayNumber: TextView = view.findViewById(R.id.tvDayNumber)
        val card: MaterialCardView = itemView as MaterialCardView // Трябва ни за цвета
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        holder.dayName.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        holder.dayNumber.text = date.dayOfMonth.toString()

        val isPast = date.toLocalDate().isBefore(LocalDate.now())
        holder.itemView.isEnabled = !isPast
        holder.itemView.alpha = if (isPast) 0.5f else 1.0f

        // Логика за оцветяване на избрания ден
        if (selectedPosition == position) {
            holder.card.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#D4AF37")))
            holder.card.setCardBackgroundColor(ColorStateList.valueOf(Color.parseColor("#FFFDF0"))) // Леко златист фон
        } else {
            holder.card.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#F0F0F0")))
            holder.card.setCardBackgroundColor(ColorStateList.valueOf(Color.WHITE))
        }

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onDateSelected(date)
        }
    }

    override fun getItemCount() = days.size

    // Функция за автоматично превъртане до определен ден
    fun getPositionForDate(date: LocalDate): Int {
        return days.indexOfFirst { it.toLocalDate() == date }
    }

    fun getDateAtPosition(position: Int): LocalDateTime {
        return days[position]
    }
}