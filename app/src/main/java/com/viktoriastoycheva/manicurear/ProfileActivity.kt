package com.viktoriastoycheva.manicurear

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.viktoriastoycheva.manicurear.utils.SessionManager

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val sessionManager = SessionManager(this)

        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvPhone = findViewById<TextView>(R.id.tvProfilePhone)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Намираме леяутите (редовете)
        val layoutName = findViewById<LinearLayout>(R.id.layoutName)
        val layoutEmail = findViewById<LinearLayout>(R.id.layoutEmail)
        val layoutPhone = findViewById<LinearLayout>(R.id.layoutPhone)
        val layoutPassword = findViewById<LinearLayout>(R.id.layoutPassword)

        layoutName.setOnClickListener {
            openEditScreen("name", tvName.text.toString())
        }

        layoutEmail.setOnClickListener {
            openEditScreen("email", tvEmail.text.toString())
        }

        layoutPhone.setOnClickListener {
            openEditScreen("phone", tvPhone.text.toString())
        }

        layoutPassword.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        // Попълваме данните от локалната памет
        refreshUI()

        btnLogout.setOnClickListener {
            sessionManager.logout() // Изчистваме сесията
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun openEditScreen(type: String, value: String) {
        val intent = Intent(this, EditFieldActivity::class.java)
        intent.putExtra("FIELD_TYPE", type)
        intent.putExtra("CURRENT_VALUE", value)
        startActivity(intent)
    }

    private fun refreshUI() {
        val sessionManager = SessionManager(this)
        findViewById<TextView>(R.id.tvProfileName).text = sessionManager.getFullName()
        findViewById<TextView>(R.id.tvProfileEmail).text = sessionManager.getEmail()
        findViewById<TextView>(R.id.tvProfilePhone).text = sessionManager.getPhone()
    }

    override fun onResume() {
        super.onResume()
        refreshUI()
    }
}