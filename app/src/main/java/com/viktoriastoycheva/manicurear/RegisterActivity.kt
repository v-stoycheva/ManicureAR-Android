package com.viktoriastoycheva.manicurear

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.viktoriastoycheva.manicurear.models.Role
import com.viktoriastoycheva.manicurear.models.User
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. ПЪРВО намираме елементите от XML по техните ID-та
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        val apiService = ApiClient.getClient().create(ApiService::class.java)

        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$".toRegex()

        btnRegister.setOnClickListener {
            // Взимаме текста от променливите, които дефинирахме по-горе
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val phone = etPhone.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            val selectedRole = Role(roleId = 3) // ROLE_CLIENT според твоята база

            if (password != confirmPassword) {
                etConfirmPassword.error = getString(R.string.error_passwords_dont_match)
                return@setOnClickListener
            }

            if (!password.matches(passwordPattern)) {
                etPassword.error = getString(R.string.error_weak_password)
                return@setOnClickListener
            }

            // 1. Проверка за празни полета
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Проверка за валиден имейл (Regex)
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
            if (!email.matches(emailPattern)) {
                etEmail.error = "Please enter a valid email address"
                return@setOnClickListener
            }

            val newUser = User(
                email = email,
                passwordHash = password,
                firstName = firstName,
                lastName = lastName,
                phone = phone,
                role = selectedRole
            )

            apiService.register(newUser).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, getString(R.string.registration_failed), Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, getString(R.string.connection_error, t.message), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}