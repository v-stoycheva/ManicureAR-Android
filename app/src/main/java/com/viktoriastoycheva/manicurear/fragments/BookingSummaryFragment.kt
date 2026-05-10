package com.viktoriastoycheva.manicurear.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.viktoriastoycheva.manicurear.BookingActivity
import com.viktoriastoycheva.manicurear.CameraActivity
import com.viktoriastoycheva.manicurear.R
import com.viktoriastoycheva.manicurear.models.Appointment
import com.viktoriastoycheva.manicurear.models.ArDesign
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.utils.SessionManager
import com.viktoriastoycheva.manicurear.viewmodels.BookingViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.util.Locale

class BookingSummaryFragment : Fragment(R.layout.fragment_booking_summary) {

    private val viewModel: BookingViewModel by activityViewModels()
    private lateinit var sessionManager: SessionManager

    // 1. Launcher за Камерата - приемаме името на дизайна
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val designName = result.data?.getStringExtra("SELECTED_DESIGN_NAME")
            val designId = result.data?.getLongExtra("SELECTED_DESIGN_ID", -1L)

            if (designId != null && designId != -1L) {
                // Вече ползваме новите имена: arDesignId и name
                viewModel.selectedDesign = com.viktoriastoycheva.manicurear.models.ArDesign(
                    arDesignId = designId,
                    name = designName ?: "Selected Design",
                    filePath = ""
                )
                updateDesignUI(designName)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        setupBaseInfo(view)

        // Бутон за Камерата
        view.findViewById<Button>(R.id.btnAddDesign).setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            intent.putExtra("IS_BOOKING_MODE", true)
            cameraLauncher.launch(intent)
        }

        // Опция за премахване на дизайна (клик върху името му го нулира)
        view.findViewById<TextView>(R.id.tvSelectedDesignName).setOnClickListener {
            if (viewModel.selectedDesign != null) {
                viewModel.selectedDesign = null
                updateDesignUI(null)
            }
        }

        view.findViewById<Button>(R.id.btnFinalConfirm).setOnClickListener {
            sendBookingToServer()
        }

        view.findViewById<ImageButton>(R.id.btnRemoveDesign).setOnClickListener {
            viewModel.selectedDesign = null
            updateDesignUI(null)
        }
    }

    private fun setupBaseInfo(view: View) {
        val service = viewModel.selectedService
        val startTime = viewModel.selectedDateTime
        val duration = service?.durationMinutes ?: 0

        view.findViewById<TextView>(R.id.tvSummaryServiceName).text = service?.title
        view.findViewById<TextView>(R.id.tvSummaryPrice).text = "${service?.price} EUR"
        view.findViewById<TextView>(R.id.tvSummaryManicurist).text = "${viewModel.selectedManicurist?.firstName} ${viewModel.selectedManicurist?.lastName}"

        // Форматиране на начален и краен час
        if (startTime != null) {
            val endTime = startTime.plusMinutes(duration.toLong())
            val dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            val formattedRange = "${startTime.format(dateFormatter)} at ${startTime.format(timeFormatter)} - ${endTime.format(timeFormatter)}"
            view.findViewById<TextView>(R.id.tvSummaryFullDateTime).text = formattedRange
        }

        // Първоначално обновяване на дизайна (ако вече е избран)
        updateDesignUI(viewModel.selectedDesign?.name)
    }

    private fun updateDesignUI(designName: String?) {
        val tvDesign = view?.findViewById<TextView>(R.id.tvSelectedDesignName)
        val btnTryAr = view?.findViewById<Button>(R.id.btnAddDesign)
        val btnRemove = view?.findViewById<ImageButton>(R.id.btnRemoveDesign)

        if (designName != null) {
            tvDesign?.text = designName
            tvDesign?.setTextColor(resources.getColor(R.color.gold, null))
            btnTryAr?.text = "CHANGE" // Променяме текста на бутона, ако вече има избран
            btnRemove?.visibility = View.VISIBLE
        } else {
            tvDesign?.text = "No design selected"
            tvDesign?.setTextColor(resources.getColor(R.color.text_hint, null))
            btnTryAr?.text = "TRY AR"
            btnRemove?.visibility = View.GONE
        }
    }
    private fun sendBookingToServer() {
        val user = viewModel.currentUser
        val service = viewModel.selectedService
        val manicurist = viewModel.selectedManicurist
        val dateTime = viewModel.selectedDateTime

        // 1. Валидация преди изпращане
        if (user == null || service == null || manicurist == null || dateTime == null) {
            Toast.makeText(context, "Incomplete booking data. Please restart the process.", Toast.LENGTH_LONG).show()
            return
        }

        // Показваме прогрес диалог или сменяме текста на бутона, за да не се натиска два пъти
        val btnConfirm = view?.findViewById<Button>(R.id.btnFinalConfirm)
        btnConfirm?.isEnabled = false
        btnConfirm?.text = "SENDING..."

        // 2. Създаване на обекта за изпращане
        // ВАЖНО: endTime се изчислява спрямо времетраенето на услугата
        val appointment = Appointment(
            appointmentId = null,
            client = user,
            manicurist = manicurist,
            service = service,
            arDesign = viewModel.selectedDesign, // Може да е null
            startTime = dateTime,
            endTime = dateTime.plusMinutes(service.durationMinutes.toLong()),
            status = "BOOKED",
            totalPrice = BigDecimal.valueOf(service.price.toDouble()),
            notes = "Mobile booking",
            createdAt = null
        )

        // 3. API повикване
        ApiClient.instance.createAppointment(appointment).enqueue(object : Callback<Appointment> {
            override fun onResponse(call: Call<Appointment>, response: Response<Appointment>) {
                if (response.isSuccessful) {
                    // Успешна резервация!
                    (activity as? BookingActivity)?.navigateToSuccess()
                } else {
                    // Сървърна грешка (напр. зает час)
                    btnConfirm?.isEnabled = true
                    btnConfirm?.text = "CONFIRM APPOINTMENT"
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(context, "Booking failed: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Appointment>, t: Throwable) {
                // Мрежова грешка
                btnConfirm?.isEnabled = true
                btnConfirm?.text = "CONFIRM APPOINTMENT"
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showSuccessDialog() {
        // Вместо просто Toast, показваме хубаво съобщение и затваряме процеса
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Success!")
            .setMessage("Your appointment has been booked successfully.")
            .setPositiveButton("OK") { _, _ ->
                // Затваряме цялата BookingActivity и се връщаме в главното меню
                activity?.finish()
            }
            .setCancelable(false)
            .show()
    }
}