package com.viktoriastoycheva.manicurear.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id") val userId: Long? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("password_hash") val passwordHash: String? = null,
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("profile_picture_url") var profilePictureUrl: String? = null,
    @SerializedName("bio") var bio: String? = null,
    @SerializedName("role") val role: Role? = null,
    @SerializedName("is_active") val isActive: Boolean = true
)