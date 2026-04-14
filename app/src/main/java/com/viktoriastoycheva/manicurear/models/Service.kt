package com.viktoriastoycheva.manicurear.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Service(
    @SerializedName("service_id") val serviceId: Long,
    val title: String,
    val description: String?,
    @SerializedName("durationMinutes") val durationMinutes: Int,
    val price: BigDecimal,
    @SerializedName("image_url") val imageUrl: String?
)