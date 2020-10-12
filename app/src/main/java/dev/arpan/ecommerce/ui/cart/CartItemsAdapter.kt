package dev.arpan.ecommerce.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.arpan.ecommerce.data.model.CartItem
import dev.arpan.ecommerce.databinding.ItemCartListBinding

typealias CartItemClickListener = ((Long) -> Unit)
typealias CartItemRemoveListener = ((Long) -> Unit)

class CartItemsAdapter(
    private val onCartItemClick: CartItemClickListener,
    private val onCartItemRemove: CartItemRemoveListener
) :
    ListAdapter<CartItem, CartItemsAdapter.CartItemViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
                oldItem.productId == newItem.productId

            override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(getItem(position), onCartItemClick, onCartItemRemove)
    }

    class CartItemViewHolder(private val binding: ItemCartListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            cartItem: CartItem,
            onClickCartItem: CartItemClickListener,
            onCartItemRemove: CartItemRemoveListener
        ) {
            binding.product = cartItem
            binding.executePendingBindings()

            binding.btnRemoveProduct.setOnClickListener {
                onCartItemRemove.invoke(cartItem.productId)
            }
            itemView.setOnClickListener {
                onClickCartItem.invoke(cartItem.productId)
            }
        }

        companion object {
            fun create(parent: ViewGroup): CartItemViewHolder {
                val binding = ItemCartListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CartItemViewHolder(binding)
            }
        }
    }
}