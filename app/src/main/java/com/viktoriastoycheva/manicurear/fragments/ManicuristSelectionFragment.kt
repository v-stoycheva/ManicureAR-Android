package com.viktoriastoycheva.manicurear.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viktoriastoycheva.manicurear.R
import com.viktoriastoycheva.manicurear.adapters.ManicuristAdapter
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.models.User
import com.viktoriastoycheva.manicurear.BookingActivity
import com.viktoriastoycheva.manicurear.viewmodels.BookingViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManicuristSelectionFragment : Fragment(R.layout.fragment_manicurist_selection) {
    private val viewModel: BookingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.manicuristsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        loadManicurists(recyclerView)
    }

    private fun loadManicurists(rv: RecyclerView) {
        ApiClient.instance.getManicurists().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()

                    rv.adapter = ManicuristAdapter(list) { selectedArtist ->
                        viewModel.selectedManicurist = selectedArtist
                        // Преминаваме към календара
                        (activity as? BookingActivity)?.onManicuristSelected(selectedArtist)
                    }
                } else {
                    Toast.makeText(context, "Error loading manicurists", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(context, "No server connection", Toast.LENGTH_SHORT).show()
            }
        })
    }
}