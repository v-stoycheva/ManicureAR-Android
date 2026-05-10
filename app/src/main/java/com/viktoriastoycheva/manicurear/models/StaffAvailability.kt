package com.viktoriastoycheva.manicurear.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class StaffAvailability(
    @SerializedName("staffAvailabilityId") val staffAvailabilityId: Long,
    @SerializedName("manicurist") val manicurist: User,
    @SerializedName("startTime") val startTime: LocalDateTime,
    @SerializedName("endTime") val endTime: LocalDateTime
)