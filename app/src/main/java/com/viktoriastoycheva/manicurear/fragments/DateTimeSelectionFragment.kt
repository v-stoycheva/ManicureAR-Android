package com.viktoriastoycheva.manicurear.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viktoriastoycheva.manicurear.R
import com.viktoriastoycheva.manicurear.adapters.CalendarAdapter
import com.viktoriastoycheva.manicurear.adapters.TimeSlotAdapter
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.BookingActivity
import com.viktoriastoycheva.manicurear.viewmodels.BookingViewModel
import com.viktoriastoycheva.manicurear.models.StaffAvailability
import com.viktoriastoycheva.manicurear.models.Appointment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateTimeSelectionFragment : Fragment(R.layout.fragment_date_time_selection) {
    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var timeSlotAdapter: TimeSlotAdapter
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var tvCurrentMonth: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
        val timeSlotsRecyclerView = view.findViewById<RecyclerView>(R.id.timeSlotsRecyclerView)
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth)

        val summaryPanel = view.findViewById<View>(R.id.bookingSummaryPanel)
        val tvSummaryDateTime = view.findViewById<TextView>(R.id.tvSummaryDateTime)
        val btnConfirmBooking = view.findViewById<Button>(R.id.btnConfirmBooking)

        calendarRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        calendarAdapter = CalendarAdapter { selectedDate ->
            summaryPanel.visibility = View.GONE
            loadAvailableSlots(selectedDate)
        }
        calendarRecyclerView.adapter = calendarAdapter

        timeSlotsRecyclerView.layoutManager = GridLayoutManager(context, 4)
        timeSlotAdapter = TimeSlotAdapter { selectedTime ->
            viewModel.selectedDateTime = selectedTime

            val duration = viewModel.selectedService?.durationMinutes ?: 60
            val endTime = selectedTime.plusMinutes(duration.toLong())

            val datePath = selectedTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH))
            val timePath = "${selectedTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"

            tvSummaryDateTime.text = "$datePath at $timePath"

            summaryPanel.visibility = View.VISIBLE
        }
        timeSlotsRecyclerView.adapter = timeSlotAdapter

        //  Бутон за потвърждение (Преминаване към Final Summary)
        btnConfirmBooking.setOnClickListener {
            Log.d("BookingFlow", "Moving to final summary screen")
            btnConfirmBooking.setOnClickListener {
                (activity as? BookingActivity)?.navigateToFinalSummary()
            }
        }

        //  Навигация със стрелки
        view.findViewById<ImageButton>(R.id.btnNextWeek).setOnClickListener {
            val layoutManager = calendarRecyclerView.layoutManager as LinearLayoutManager
            val firstVisible = layoutManager.findFirstVisibleItemPosition()
            val targetPos = (firstVisible + 7).coerceAtMost(calendarAdapter.itemCount - 1)
            calendarRecyclerView.smoothScrollToPosition(targetPos)
        }

        view.findViewById<ImageButton>(R.id.btnPrevWeek).setOnClickListener {
            val layoutManager = calendarRecyclerView.layoutManager as LinearLayoutManager
            val firstVisible = layoutManager.findFirstVisibleItemPosition()
            val targetPos = (firstVisible - 7).coerceAtLeast(0)
            calendarRecyclerView.smoothScrollToPosition(targetPos)
        }

        calendarRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisiblePos = layoutManager.findFirstVisibleItemPosition()
                if (firstVisiblePos != -1) {
                    val date = calendarAdapter.getDateAtPosition(firstVisiblePos)
                    val formatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale.ENGLISH)
                    val monthYear = date.format(formatter)
                    tvCurrentMonth.text = monthYear.replaceFirstChar { it.uppercase() }
                }
            }
        })

        findFirstAvailability()
    }

    private fun findFirstAvailability() {
        val manicuristId = viewModel.selectedManicurist?.userId ?: return

        ApiClient.instance.getManicuristAvailability(manicuristId).enqueue(object : Callback<List<StaffAvailability>> {
            override fun onResponse(call: Call<List<StaffAvailability>>, response: Response<List<StaffAvailability>>) {
                val availability = response.body() ?: emptyList()
                if (availability.isNotEmpty()) {
                    val futureAvailability = availability.filter { !it.startTime.toLocalDate().isBefore(LocalDate.now()) }
                    if (futureAvailability.isNotEmpty()) {
                        val firstAvailableDate = futureAvailability.first().startTime
                        val pos = calendarAdapter.getPositionForDate(firstAvailableDate.toLocalDate())
                        if (pos != -1) {
                            calendarRecyclerView.scrollToPosition(pos)
                            loadAvailableSlots(firstAvailableDate)
                        }
                    }
                } else {
                    val todayPos = calendarAdapter.getPositionForDate(LocalDate.now())
                    if (todayPos != -1) calendarRecyclerView.scrollToPosition(todayPos)
                }
            }
            override fun onFailure(call: Call<List<StaffAvailability>>, t: Throwable) {
                Log.e("NetworkError", "Reason: ${t.message}")
            }
        })
    }

    private fun loadAvailableSlots(date: LocalDateTime) {
        val manicuristId = viewModel.selectedManicurist?.userId ?: return

        ApiClient.instance.getManicuristAvailability(manicuristId).enqueue(object : Callback<List<StaffAvailability>> {
            override fun onResponse(call: Call<List<StaffAvailability>>, response: Response<List<StaffAvailability>>) {
                val dayAvailability = response.body()?.filter { it.startTime.toLocalDate() == date.toLocalDate() }

                if (dayAvailability.isNullOrEmpty()) {
                    timeSlotAdapter.submitList(emptyList())
                    return
                }

                ApiClient.instance.getManicuristAppointments(manicuristId).enqueue(object : Callback<List<Appointment>> {
                    override fun onResponse(call: Call<List<Appointment>>, response: Response<List<Appointment>>) {
                        val bookings = response.body() ?: emptyList()
                        val duration = viewModel.selectedService?.durationMinutes ?: 60

                        val allPossibleSlots = mutableListOf<LocalDateTime>()
                        dayAvailability.forEach { period ->
                            allPossibleSlots.addAll(generateTimeSlots(period.startTime, period.endTime, duration))
                        }

                        val freeSlots = allPossibleSlots.filter { slot ->
                            val slotEnd = slot.plusMinutes(duration.toLong())
                            bookings.none { b ->
                                b.status != "CANCELLED" && slot.isBefore(b.endTime) && b.startTime.isBefore(slotEnd)
                            }
                        }
                        timeSlotAdapter.submitList(freeSlots)
                    }
                    override fun onFailure(call: Call<List<Appointment>>, t: Throwable) {}
                })
            }
            override fun onFailure(call: Call<List<StaffAvailability>>, t: Throwable) {}
        })
    }

    private fun generateTimeSlots(start: LocalDateTime, end: LocalDateTime, duration: Int): List<LocalDateTime> {
        val slots = mutableListOf<LocalDateTime>()
        var current = start
        val now = LocalDateTime.now()
        val lastPossibleStart = end.minusMinutes(duration.toLong())

        while (!current.isAfter(lastPossibleStart)) {
            if (current.toLocalDate() == now.toLocalDate()) {
                if (current.isAfter(now.plusMinutes(15))) {
                    slots.add(current)
                }
            } else {
                slots.add(current)
            }
            current = current.plusMinutes(15)
        }
        return slots
    }
}