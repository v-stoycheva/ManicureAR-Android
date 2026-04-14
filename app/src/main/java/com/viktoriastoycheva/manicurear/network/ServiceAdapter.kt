package com.viktoriastoycheva.manicurear.network

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.viktoriastoycheva.manicurear.R
import com.viktoriastoycheva.manicurear.models.Service

class ServiceAdapter(private val services: List<Service>) :
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    // ViewHolder: Свързва се с елементите в item_service.xml
    class ServiceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvServiceTitle)
        val description: TextView = view.findViewById(R.id.tvServiceDescription)
        val price: TextView = view.findViewById(R.id.tvServicePrice)
        val duration: TextView = view.findViewById(R.id.tvServiceDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]

        holder.title.text = service.title
        holder.description.text = service.description
        holder.price.text = "${service.price} EUR"
        holder.duration.text = "${service.durationMinutes} min"
    }

    override fun getItemCount(): Int = services.size
}