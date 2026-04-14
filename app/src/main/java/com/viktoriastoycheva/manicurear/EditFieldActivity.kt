package com.viktoriastoycheva.manicurear

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.viktoriastoycheva.manicurear.models.User
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.network.ApiService
import com.viktoriastoycheva.manicurear.utils.SessionManager

class EditFieldActivity : AppCompatActivity() {

    private lateinit var fieldType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_field)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnSave = findViewById<TextView>(R.id.btnSave)
        val tvEditTitle = findViewById<TextView>(R.id.tvEditTitle)
        val tvLabel = findViewById<TextView>(R.id.tvLabel)
        val etValue = findViewById<EditText>(R.id.etValue)

        // 1. Взимаме данните от Intent
        fieldType = intent.getStringExtra("FIELD_TYPE") ?: "name"
        val currentValue = intent.getStringExtra("CURRENT_VALUE") ?: ""

        // 2. Настройваме екрана според типа на полето
        setupUI(tvEditTitle, tvLabel, etValue, currentValue)

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val newValue = etValue.text.toString().trim()
            if (newValue.isEmpty()) {
                etValue.error = "Field cannot be empty"
            } else {
                saveDataToBackend(newValue)
            }
        }
    }

    private fun setupUI(title: TextView, label: TextView, input: EditText, value: String) {
        input.setText(value)
        input.setSelection(input.text.length)

        when (fieldType) {
            "name" -> {
                title.text = "Edit Name"
                label.text = "Enter your full name"
                input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            }
            "email" -> {
                title.text = "Edit Email"
                label.text = "Enter your new email address"
                input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            "phone" -> {
                title.text = "Edit Phone"
                label.text = "Enter your phone number"
                input.inputType = InputType.TYPE_CLASS_PHONE
            }
        }
        input.setSelection(input.text.length) // Курсорът да отиде най-отзад
        input.isFocusableInTouchMode = true
        input.requestFocus() // Автоматично активира полето за писане
    }

    private fun saveDataToBackend(value: String) {
        var firstName: String? = null
        var lastName: String? = null
        var email: String? = null
        var phone: String? = null

        when (fieldType) {
            "name" -> {
                val parts = value.split(" ", limit = 2)
                firstName = parts[0]
                lastName = if (parts.size > 1) parts[1] else ""
            }
            "email" -> email = value
            "phone" -> phone = value
        }

        updateUserOnServer(firstName, lastName, email, phone)
    }

    private fun updateUserOnServer(fName: String?, lName: String?, mail: String?, ph: String?) {
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()

        if (userId == -1L) {
            Toast.makeText(this, "User session error", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiClient.getClient().create(ApiService::class.java)

        // КРИТИЧНО: Пращаме обекта само с полетата, които искаме да променим.
        // Останалите остават null, за да не ги презапише бекендът.
        val updatedUser = User(
            firstName = fName,
            lastName = lName,
            email = mail,
            phone = ph
        )

        apiService.updateUser(userId, updatedUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val returnedUser = response.body()
                    returnedUser?.let {
                        // Обновяваме локалната сесия с това, което сървърът ни върна
                        sessionManager.saveUser(
                            userId, // запазваме си оригиналното ID
                            it.firstName ?: sessionManager.getFirstName(),
                            it.lastName ?: "", // Тук може да добавиш getLastName() в SessionManager
                            it.email ?: sessionManager.getEmail() ?: "",
                            it.phone ?: sessionManager.getPhone()
                        )
                    }
                    Toast.makeText(this@EditFieldActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditFieldActivity, "Failed to update: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@EditFieldActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}