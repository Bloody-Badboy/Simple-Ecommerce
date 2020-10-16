/*
 * Copyright 2020 Arpan Sarkar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
