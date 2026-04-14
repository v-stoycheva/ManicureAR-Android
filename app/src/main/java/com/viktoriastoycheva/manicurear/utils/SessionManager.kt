package com.viktoriastoycheva.manicurear.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
    }

    // Записване на данните след успешен Login
    fun saveUser(userId: Long, firstName: String?, lastName: String?, email: String?, phone: String?) {
        val editor = prefs.edit()
        editor.putLong(KEY_USER_ID, userId)
        editor.putString(KEY_FIRST_NAME, firstName)
        editor.putString(KEY_LAST_NAME, lastName)
        editor.putString(KEY_EMAIL, email ?: "") // Ако е null, записваме празен текст
        editor.putString(KEY_PHONE, phone)
        editor.apply()
    }

    // Вземане на името за Dashboard/Profile
    fun getFirstName(): String? = prefs.getString(KEY_FIRST_NAME, "User")

    fun getUserId(): Long = prefs.getLong("user_id", -1L)
    fun getFullName(): String {
        val first = prefs.getString("first_name", "") ?: ""
        val last = prefs.getString("last_name", "") ?: ""
        return if (last.isEmpty()) first else "$first $last"
    }
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getPhone(): String? = prefs.getString(KEY_PHONE, "N/A")

    // Изчистване при Logout
    fun logout() {
        prefs.edit().clear().apply()
    }
}