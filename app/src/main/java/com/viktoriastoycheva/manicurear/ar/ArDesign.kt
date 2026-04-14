package com.viktoriastoycheva.manicurear.ar

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ar_designs")
data class ArDesign(
    @PrimaryKey(autoGenerate = true) val ar_design_id: Int = 0,
    val category_id: Int,
    val name: String,
    val file_path: String,
    val is_active: Boolean = true
)