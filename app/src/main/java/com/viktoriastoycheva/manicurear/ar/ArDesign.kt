package com.viktoriastoycheva.manicurear.ar

import com.google.gson.annotations.SerializedName

data class ArDesign(
    @SerializedName("arDesignId")
    val ar_design_id: Long,

    @SerializedName("category")
    val category: Any? = null,

    val name: String,

    @SerializedName("filePath")
    val file_path: String,

    @SerializedName("isActive")
    val is_active: Boolean = true
)