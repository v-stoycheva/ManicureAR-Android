package com.viktoriastoycheva.manicurear

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.viktoriastoycheva.manicurear.models.User
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.network.ApiService
import com.viktoriastoycheva.manicurear.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        // ПРОВЕРКА: Имаме ли вече логнат потребител?
        if (sessionManager.getEmail() != null) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish() // Затваряме LoginActivity, за да не може да се върнем към нея
            return // Спираме изпълнението на останалия код надолу
        }

        setContentView(R.layout.activity_login)

        // Initialize UI components
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        val apiService = ApiClient.instance

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Basic validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // API call to login
            apiService.login(email, password).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()

                        val sessionManager = SessionManager(this@LoginActivity)
                        user?.let {
                            sessionManager.saveUser(it.userId ?: 0, it.firstName, it.lastName, it.email, it.phone)
                        }

                        Toast.makeText(this@LoginActivity, getString(R.string.welcome_message, user?.firstName), Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Error 401 (Unauthorized) or 404
                        Toast.makeText(this@LoginActivity, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    // Network or server connection issues
                    Toast.makeText(this@LoginActivity, getString(R.string.connection_error, t.message), Toast.LENGTH_LONG).show()
                }
            })
        }

        // Navigate to Register screen
        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}