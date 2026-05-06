package com.viktoriastoycheva.manicurear.models

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Service(
    @SerializedName("serviceId")
    val serviceId: Long,

    val title: String,

    val description: String?,

    val durationMinutes: Int,

    val price: BigDecimal,

    @SerializedName("imageUrl")
    val imageUrl: String?
)