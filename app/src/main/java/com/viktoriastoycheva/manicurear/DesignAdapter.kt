package com.viktoriastoycheva.manicurear.ar

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.viktoriastoycheva.manicurear.R

class DesignAdapter(
    private var designs: List<ArDesign>,
    private val onDesignClick: (ArDesign) -> Unit
) : RecyclerView.Adapter<DesignAdapter.DesignViewHolder>() {

    class DesignViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivDesignImage)
        val tvName: TextView = view.findViewById(R.id.tvDesignName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesignViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_design, parent, false)
        return DesignViewHolder(view)
    }

    override fun onBindViewHolder(holder: DesignViewHolder, position: Int) {
        val design = designs[position]
        holder.tvName.text = design.name

        // Използваме Glide за зареждане от Firebase URL
        Glide.with(holder.itemView.context)
            .load(design.file_path)
            .timeout(60000)
            .placeholder(R.drawable.nail_texture)
            .error(R.drawable.nail_texture)
            .centerCrop() // Запълва квадратчето по-добре
            .into(holder.ivImage)

        holder.itemView.setOnClickListener { onDesignClick(design) }
    }

    override fun getItemCount() = designs.size

    fun updateList(newList: List<ArDesign>) {
        this.designs = newList
        notifyDataSetChanged()
    }
}