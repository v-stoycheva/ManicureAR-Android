package com.viktoriastoycheva.manicurear.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.time.LocalDateTime

data class Appointment(
    @SerializedName("appointmentId") val appointmentId: Long?,
    @SerializedName("client") val client: User,
    @SerializedName("manicurist") val manicurist: User,
    @SerializedName("service") val service: Service,
    @SerializedName("arDesign") val arDesign: ArDesign?,
    @SerializedName("startTime") val startTime: LocalDateTime,
    @SerializedName("endTime") val endTime: LocalDateTime,
    @SerializedName("status") val status: String = "BOOKED",
    @SerializedName("totalPrice") val totalPrice: BigDecimal?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("createdAt") val createdAt: LocalDateTime? = null
)