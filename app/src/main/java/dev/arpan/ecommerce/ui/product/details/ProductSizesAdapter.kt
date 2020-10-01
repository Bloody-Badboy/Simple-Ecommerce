package dev.arpan.ecommerce.ui.product.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.arpan.ecommerce.databinding.ItemProductSizeListBinding

class ProductSizesAdapter : RecyclerView.Adapter<ProductSizesAdapter.ProductSizeItemViewHolder>() {

    var sizes: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSizeItemViewHolder {
        return ProductSizeItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ProductSizeItemViewHolder, position: Int) {
       holder.bind(sizes[position])
    }

    override fun getItemCount() = sizes.size

    class ProductSizeItemViewHolder(private val binding: ItemProductSizeListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(size: String) {
            binding.tvSize.text = size
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