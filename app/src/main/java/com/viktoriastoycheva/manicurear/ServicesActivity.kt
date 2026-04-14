package com.viktoriastoycheva.manicurear

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viktoriastoycheva.manicurear.models.Service
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.network.ApiService
import com.viktoriastoycheva.manicurear.network.ServiceAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServicesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        val rvServices = findViewById<RecyclerView>(R.id.rvServices)
        rvServices.layoutManager = LinearLayoutManager(this)

        val apiService = ApiClient.getClient().create(ApiService::class.java)

        // Извличане на услугите от Backend-а
        apiService.getAllServices().enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                if (response.isSuccessful) {
                    val servicesList = response.body() ?: emptyList()
                    // Слагаме данните в адаптера
                    rvServices.adapter = ServiceAdapter(servicesList)
                } else {
                    Toast.makeText(this@ServicesActivity, "Failed to load services", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Service>>, t: Throwable) {
                Toast.makeText(this@ServicesActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}