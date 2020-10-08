package dev.arpan.ecommerce.ui.product.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.arpan.ecommerce.databinding.ItemProductSizeListBinding
import kotlin.properties.Delegates

class ProductSizesAdapter : RecyclerView.Adapter<ProductSizesAdapter.ProductSizeItemViewHolder>() {

    var sizes: List<String> = emptyList()
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                selectedPosition = 0
            }
            notifyDataSetChanged()
        }

    private var selectedPosition by Delegates.observable(-1) { _, oldPos, newPos ->
        if (newPos in sizes.indices) {
            notifyItemChanged(oldPos)
            notifyItemChanged(newPos)
        }
    }

    val selectedSize: String?
        get() {
            return if (selectedPosition < 0) null else sizes[selectedPosition]
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSizeItemViewHolder {
        return ProductSizeItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ProductSizeItemViewHolder, position: Int) {
        holder.bind(sizes[position], position == selectedPosition)
        holder.itemView.setOnClickListener {
            selectedPosition = position
        }
    }

    override fun getItemCount() = sizes.size

    class ProductSizeItemViewHolder(private val binding: ItemProductSizeListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(size: String, isSelected: Boolean) {
            binding.tvSize.text = size
            binding.tvSize.isSelected = isSelected
        }

        companion object {
            fun create(parent: ViewGroup): ProductSizeItemViewHolder {
                val binding = ItemProductSizeListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ProductSizeItemViewHolder(binding)
            }
        }
    }
}