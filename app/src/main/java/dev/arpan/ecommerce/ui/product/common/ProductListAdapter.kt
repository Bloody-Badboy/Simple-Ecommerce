package dev.arpan.ecommerce.ui.product.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.arpan.ecommerce.bindImageUrl
import dev.arpan.ecommerce.data.model.ProductItem
import dev.arpan.ecommerce.databinding.ItemProductListBinding

typealias ProductClickListener = ((Long) -> Unit)

class ProductListAdapter(private val onProductClick: ProductClickListener) :
    ListAdapter<ProductItem, RecyclerView.ViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<ProductItem>() {
            override fun areItemsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(oldItem: ProductItem, newItem: ProductItem): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ProductItemViewHolder).bind(onProductClick, getItem(position))
    }

    class ProductItemViewHolder(private val binding: ItemProductListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(onProductClick: ProductClickListener, productItem: ProductItem) {
            binding.product = productItem
            binding.ivProductImage.bindImageUrl(productItem.imageUrl)
            binding.executePendingBindings()

            itemView.setOnClickListener {
                onProductClick.invoke(productItem.productId)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ProductItemViewHolder {
                val binding = ItemProductListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ProductItemViewHolder(binding)
            }
        }
    }
}
