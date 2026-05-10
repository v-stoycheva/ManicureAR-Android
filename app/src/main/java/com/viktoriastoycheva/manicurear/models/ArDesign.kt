package com.viktoriastoycheva.manicurear.models

import com.google.gson.annotations.SerializedName

data class ArDesign(
    @SerializedName("arDesignId")
    val arDesignId: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("filePath")
    val filePath: String,

    @SerializedName("isActive")
    val isActive: Boolean = true
)