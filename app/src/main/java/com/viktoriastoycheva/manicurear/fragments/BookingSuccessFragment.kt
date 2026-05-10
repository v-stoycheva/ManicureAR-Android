package com.viktoriastoycheva.manicurear.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.viktoriastoycheva.manicurear.R

class BookingSuccessFragment : Fragment(R.layout.fragment_booking_success) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animationView = view.findViewById<LottieAnimationView>(R.id.lavSuccess)
        animationView.playAnimation()

        view.findViewById<Button>(R.id.btnBackToHome).setOnClickListener {
            activity?.finish()
        }
    }
}