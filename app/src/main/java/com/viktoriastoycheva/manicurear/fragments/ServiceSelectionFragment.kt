package com.viktoriastoycheva.manicurear.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viktoriastoycheva.manicurear.R
import com.viktoriastoycheva.manicurear.adapters.ServiceAdapter
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.models.Service
import com.viktoriastoycheva.manicurear.BookingActivity
import com.viktoriastoycheva.manicurear.viewmodels.BookingViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ServiceSelectionFragment : Fragment(R.layout.fragment_service_selection) {

    private val viewModel: BookingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Намираме RecyclerView в XML-а на фрагмента
        val recyclerView = view.findViewById<RecyclerView>(R.id.servicesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        loadServices(recyclerView)
    }

    private fun loadServices(rv: RecyclerView) {
        // Използваме твоя ApiClient.instance
        ApiClient.instance.getAllServices().enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                if (response.isSuccessful) {
                    val servicesList = response.body() ?: emptyList()

                    // Слагаме данните в твоя ServiceAdapter, като добавяме логика за клик
                    rv.adapter = ServiceAdapter(servicesList) { selectedService ->
                        // 1. Запазваме избора във ViewModel
                        viewModel.selectedService = selectedService

                        // 2. Казваме на главното Activity да ни прехвърли към следващата стъпка
                        (activity as? BookingActivity)?.onServiceSelected(selectedService)
                    }
                } else {
                    Toast.makeText(context, "Неуспешно зареждане на услугите", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Service>>, t: Throwable) {
                Toast.makeText(context, "Грешка: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}