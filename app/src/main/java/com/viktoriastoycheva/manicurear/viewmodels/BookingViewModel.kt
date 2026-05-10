package com.viktoriastoycheva.manicurear.viewmodels

import androidx.lifecycle.ViewModel
import com.viktoriastoycheva.manicurear.models.ArDesign
import com.viktoriastoycheva.manicurear.models.Service
import com.viktoriastoycheva.manicurear.models.User
import java.time.LocalDateTime

class BookingViewModel : ViewModel() {
    var selectedService: Service? = null
    var selectedManicurist: User? = null
    var selectedDateTime: LocalDateTime? = null
    var selectedDesign: ArDesign? = null
    var currentUser: User? = null
}