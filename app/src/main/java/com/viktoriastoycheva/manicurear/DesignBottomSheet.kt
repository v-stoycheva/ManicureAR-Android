package com.viktoriastoycheva.manicurear.ar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.viktoriastoycheva.manicurear.R
import com.viktoriastoycheva.manicurear.adapters.DesignAdapter
import com.viktoriastoycheva.manicurear.models.ArDesign
import com.viktoriastoycheva.manicurear.network.ApiClient
import com.viktoriastoycheva.manicurear.network.ApiService
import com.viktoriastoycheva.manicurear.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DesignBottomSheet(
    private val onDesignSelected: (ArDesign) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var adapter: DesignAdapter
    private var allDesigns: List<ArDesign> = listOf()
    private var favoriteDesigns: List<ArDesign> = listOf()
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.layout_design_bottom_sheet, container, false)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val rvDesigns = view.findViewById<RecyclerView>(R.id.rvDesigns)

        apiService = ApiClient.instance

        adapter = DesignAdapter(listOf()) { design ->
            onDesignSelected(design)
            dismiss()
        }

        rvDesigns.layoutManager = GridLayoutManager(context, 3)
        rvDesigns.adapter = adapter

        loadDataFromServer()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> adapter.updateList(allDesigns)
                    1 -> adapter.updateList(favoriteDesigns)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        return view
    }

    private fun loadDataFromServer() {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()

        // 1. Взимаме всички активни дизайни от сървъра
        apiService.getAllArDesigns().enqueue(object : Callback<List<ArDesign>> {
            override fun onResponse(call: Call<List<ArDesign>>, response: Response<List<ArDesign>>) {
                if (response.isSuccessful) {
                    allDesigns = response.body() ?: emptyList()
                    adapter.updateList(allDesigns)
                }
            }
            override fun onFailure(call: Call<List<ArDesign>>, t: Throwable) {
                Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
            }
        })

        // 2. Взимаме любимите дизайни за конкретния потребител[cite: 18]
        if (userId != -1L) {
            apiService.getFavoriteDesigns(userId).enqueue(object : Callback<List<ArDesign>> {
                override fun onResponse(call: Call<List<ArDesign>>, response: Response<List<ArDesign>>) {
                    if (response.isSuccessful) {
                        favoriteDesigns = response.body() ?: emptyList()
                    }
                }
                override fun onFailure(call: Call<List<ArDesign>>, t: Throwable) {
                    // Тъхъл пропуск, ако любимите не се заредят веднага
                }
            })
        }
    }

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog
}