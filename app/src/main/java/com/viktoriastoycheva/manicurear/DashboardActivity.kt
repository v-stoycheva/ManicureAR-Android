package com.viktoriastoycheva.manicurear

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.viktoriastoycheva.manicurear.utils.SessionManager

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val btnTryAR = findViewById<Button>(R.id.btnTryAR)
        val btnBook = findViewById<Button>(R.id.btnBook)
        val btnProfile = findViewById<Button>(R.id.btnProfile)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnTryAR.setOnClickListener {
            // Връзка към AR камерата
            startActivity(Intent(this, CameraActivity::class.java))
        }

        btnBook.setOnClickListener {
            startActivity(Intent(this, ServicesActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        btnLogout.setOnClickListener {
            val sessionManager = SessionManager(this)
            sessionManager.logout()

            val intent = Intent(this, LoginActivity::class.java)
            // Тези флагове изчистват цялата история на навигацията
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


    }
}