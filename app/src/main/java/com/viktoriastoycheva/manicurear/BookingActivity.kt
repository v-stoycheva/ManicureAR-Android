package com.viktoriastoycheva.manicurear

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.viktoriastoycheva.manicurear.fragments.BookingSuccessFragment
import com.viktoriastoycheva.manicurear.fragments.BookingSummaryFragment
import com.viktoriastoycheva.manicurear.fragments.DateTimeSelectionFragment
import com.viktoriastoycheva.manicurear.fragments.ManicuristSelectionFragment
import com.viktoriastoycheva.manicurear.fragments.ServiceSelectionFragment
import com.viktoriastoycheva.manicurear.models.Service
import com.viktoriastoycheva.manicurear.models.User
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.utils.SessionManager
import com.viktoriastoycheva.manicurear.viewmodels.BookingViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingActivity : AppCompatActivity() {
    private val bookingViewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()

        if (userId != -1L) {
            ApiClient.instance.getUserById(userId).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        bookingViewModel.currentUser = response.body()
                    }
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("BookingActivity", "Failed to load user")
                }
            })
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.booking_fragment_container, ServiceSelectionFragment())
                .commit()
        }
    }

    fun onServiceSelected(service: Service) {
        bookingViewModel.selectedService = service
        replaceFragment(ManicuristSelectionFragment())
    }

    fun onManicuristSelected(manicurist: User) {
        bookingViewModel.selectedManicurist = manicurist
        replaceFragment(DateTimeSelectionFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.booking_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToFinalSummary() {
        val fragment = BookingSummaryFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.booking_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToSuccess() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.booking_fragment_container, BookingSuccessFragment())
            .commit()
    }

}