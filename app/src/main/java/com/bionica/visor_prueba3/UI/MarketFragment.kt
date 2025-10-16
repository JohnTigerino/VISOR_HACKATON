package com.bionica.visor_prueba3.UI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bionica.visor_prueba3.R
import com.bionica.visor_prueba3.data.Product

class ProductAdapter(
    private val onClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.VH>(DIFF) {


    object DIFF : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }


    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val img: ImageView = view.findViewById(R.id.img)
        private val name: TextView = view.findViewById(R.id.txtName)
        private val price: TextView = view.findViewById(R.id.txtPrice)
        private val desc: TextView = view.findViewById(R.id.txtDesc)
        fun bind(p: Product) {
            img.load(p.imageUrl)
            name.text = p.name
            val unitText = if (p.unit.isNotBlank()) " / ${p.unit}" else ""
            price.text = "C$ ${"%.2f".format(p.price)}$unitText"
            desc.text = p.description
            itemView.setOnClickListener { onClick(p) }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return VH(v)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}