package com.viktoriastoycheva.manicurear

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viktoriastoycheva.manicurear.adapters.AppointmentAdapter
import com.viktoriastoycheva.manicurear.models.Appointment
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter

class MyAppointmentsActivity : AppCompatActivity() {

    private lateinit var adapter: AppointmentAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var tvEmptyState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_appointments)

        sessionManager = SessionManager(this)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        val rvAppointments = findViewById<RecyclerView>(R.id.rvAppointments)

        rvAppointments.layoutManager = LinearLayoutManager(this)
        adapter = AppointmentAdapter(listOf()) { appointment ->
            showCancelConfirmation(appointment)
        }
        rvAppointments.adapter = adapter

        loadAppointments()
    }

    private fun loadAppointments() {
        val userId = sessionManager.getUserId()
        if (userId == -1L) return

        ApiClient.instance.getClientHistory(userId).enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(call: Call<List<Appointment>>, response: Response<List<Appointment>>) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    if (list.isEmpty()) {
                        tvEmptyState.visibility = View.VISIBLE
                    } else {
                        tvEmptyState.visibility = View.GONE
                        // Сортираме ги така, че най-новите да са най-отгоре
                        adapter.updateList(list.sortedByDescending { it.startTime })
                    }
                }
            }

            override fun onFailure(call: Call<List<Appointment>>, t: Throwable) {
                Toast.makeText(this@MyAppointmentsActivity, "Error loading appointments", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showCancelConfirmation(appointment: Bundle?) {
    }

    // Реалният метод за потвърждение:
    private fun showCancelConfirmation(appointment: Appointment) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Appointment")
            .setMessage("Are you sure you want to cancel your manicure at ${appointment.startTime.format(
                DateTimeFormatter.ofPattern("HH:mm"))}?")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                cancelAppointment(appointment.appointmentId!!)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelAppointment(id: Long) {
        ApiClient.instance.cancelAppointment(id).enqueue(object : Callback<Void> { // Променено на Void
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MyAppointmentsActivity, "Cancelled successfully", Toast.LENGTH_SHORT).show()
                    loadAppointments()
                } else {
                    Toast.makeText(this@MyAppointmentsActivity, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API_ERROR", "Cancel failed: ${t.message}")
                loadAppointments()
            }
        })
    }
}