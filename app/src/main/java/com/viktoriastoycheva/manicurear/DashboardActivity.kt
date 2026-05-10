package com.viktoriastoycheva.manicurear

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.viktoriastoycheva.manicurear.models.Appointment
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val btnTryAR = findViewById<Button>(R.id.btnTryAR)
        val btnBook = findViewById<Button>(R.id.btnBook)
        val btnMyAppointments = findViewById<Button>(R.id.btnMyAppointments)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnTryAR.setOnClickListener {
            // Връзка към AR камерата
            startActivity(Intent(this, CameraActivity::class.java))
        }

        btnBook.setOnClickListener {
            startActivity(Intent(this, BookingActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnMyAppointments.setOnClickListener {
            startActivity(Intent(this, MyAppointmentsActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val sessionManager = SessionManager(this)
            sessionManager.logout()

            val intent = Intent(this, LoginActivity::class.java)
            // Тези флагове изчистват цялата история на навигацията
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        loadNextAppointment()
    }

    private fun loadNextAppointment() {
        val userId = SessionManager(this).getUserId()
        if (userId == -1L) return

        ApiClient.instance.getClientHistory(userId).enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(call: Call<List<Appointment>>, response: Response<List<Appointment>>) {
                if (response.isSuccessful) {
                    val now = LocalDateTime.now()
                    // Търсим най-близкия предстоящ час, който не е отменен
                    val nextApp = response.body()
                        ?.filter { it.startTime.isAfter(now) && it.status == "BOOKED" }
                        ?.minByOrNull { it.startTime }

                    updateNextAppointmentUI(nextApp)
                }
            }

            override fun onFailure(call: Call<List<Appointment>>, t: Throwable) {
                // Ако няма интернет или API-то падне, скриваме картата, за да не пречи
                findViewById<View>(R.id.includedNextAppointment).visibility = View.GONE
            }
        })
    }

    private fun updateNextAppointmentUI(appointment: Appointment?) {
        val card = findViewById<View>(R.id.includedNextAppointment)
        val tvService = card.findViewById<TextView>(R.id.tvNextService)
        val tvDateTime = card.findViewById<TextView>(R.id.tvNextDateTime)
        val tvArtist = card.findViewById<TextView>(R.id.tvNextArtist)
        val detailsLayout = card.findViewById<View>(R.id.layoutNextDetails)

        if (appointment != null) {
            card.visibility = View.VISIBLE
            tvService.text = appointment.service.title
            val formatter = DateTimeFormatter.ofPattern("MMM d 'at' HH:mm", Locale.ENGLISH)
            tvDateTime.text = appointment.startTime.format(formatter)
            tvArtist.text = "with ${appointment.manicurist.firstName}"
            detailsLayout.visibility = View.VISIBLE

            // Опция: При клик върху картата да отваря списъка с всички резервации
            card.setOnClickListener {
                startActivity(Intent(this, MyAppointmentsActivity::class.java))
            }
        } else {
            // Ако няма предстоящ час, можем или да скрием картата,
            // или да я оставим като покана за нова резервация
            tvService.text = "Ready for a new design?"
            detailsLayout.visibility = View.GONE
            card.setOnClickListener {
                startActivity(Intent(this, BookingActivity::class.java))
            }
        }
    }
}