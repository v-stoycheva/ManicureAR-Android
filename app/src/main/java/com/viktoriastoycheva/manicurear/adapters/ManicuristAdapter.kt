package com.viktoriastoycheva.manicurear.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.viktoriastoycheva.manicurear.R
import com.viktoriastoycheva.manicurear.models.User

class ManicuristAdapter(
    private val manicurists: List<User>,
    private val onManicuristClick: (User) -> Unit
) : RecyclerView.Adapter<ManicuristAdapter.ManicuristViewHolder>() {

    class ManicuristViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvManicuristName)
        val bio: TextView = view.findViewById(R.id.tvManicuristBio)
        val photo: ImageView = view.findViewById(R.id.ivManicuristPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManicuristViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_manicurist, parent, false)
        return ManicuristViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManicuristViewHolder, position: Int) {
        val artist = manicurists[position]

        holder.name.text = "${artist.firstName} ${artist.lastName}"
        holder.bio.text = artist.bio ?: "Manicurist"

        // Зареждаме снимката с Glide (ако има такава)
        if (!artist.profilePictureUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(artist.profilePictureUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.photo)
        }

        holder.itemView.setOnClickListener { onManicuristClick(artist) }
    }

    override fun getItemCount(): Int = manicurists.size
}