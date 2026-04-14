package com.viktoriastoycheva.manicurear

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.network.ApiService
import com.viktoriastoycheva.manicurear.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnDone = findViewById<TextView>(R.id.btnDone)
        val etCurrent = findViewById<EditText>(R.id.etCurrentPassword)
        val etNew = findViewById<EditText>(R.id.etNewPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmNewPassword)

        btnBack.setOnClickListener { finish() }

        btnDone.setOnClickListener {
            val current = etCurrent.text.toString()
            val new = etNew.text.toString()
            val confirm = etConfirm.text.toString()

            // Валидация
            if (current.isEmpty() || new.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (new != confirm) {
                etConfirm.error = "Passwords do not match"
                return@setOnClickListener
            }

            if (!new.matches(passwordPattern)) {
                etNew.error = "Password too weak"
                return@setOnClickListener
            }

            changePasswordOnServer(current, new)
        }
    }

    private fun changePasswordOnServer(current: String, new: String) {
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()
        val apiService = ApiClient.getClient().create(ApiService::class.java)

        apiService.changePassword(userId, current, new).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ChangePasswordActivity, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to change password"
                    Toast.makeText(this@ChangePasswordActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ChangePasswordActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}