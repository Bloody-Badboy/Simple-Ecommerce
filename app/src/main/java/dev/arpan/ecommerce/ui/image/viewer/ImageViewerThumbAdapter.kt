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

package dev.arpan.ecommerce.ui.image.viewer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.arpan.ecommerce.bindImageUrl
import dev.arpan.ecommerce.databinding.ItemImageViewerThumbBinding
import kotlin.properties.Delegates

class ImageViewerThumbAdapter(private val onItemClick: ((position: Int) -> Unit)) :
    RecyclerView.Adapter<ImageViewerThumbAdapter.ProductImageSliderThumbViewHolder>() {

    var images: List<String> = emptyList()
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                selectedPosition = 0
            }
            notifyDataSetChanged()
        }

    var selectedPosition by Delegates.observable(-1) { _, oldPos, newPos ->
        if (newPos in images.indices) {
            notifyItemChanged(oldPos)
            notifyItemChanged(newPos)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductImageSliderThumbViewHolder {
        return ProductImageSliderThumbViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ProductImageSliderThumbViewHolder, position: Int) {
        holder.bind(images[position], position == selectedPosition)
        holder.itemView.setOnClickListener {
            selectedPosition = position
            onItemClick.invoke(position)
        }
    }

    override fun getItemCount() = images.size

    class ProductImageSliderThumbViewHolder(private val binding: ItemImageViewerThumbBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String, isSelected: Boolean) {
            binding.ivPhoto.bindImageUrl(imageUrl)
            if (!isSelected) {
                binding.ivPhoto.animate()
                    .scaleX(0.7f)
                    .scaleY(0.7f)
                    .setDuration(200L)
                    .start()
            } else {
                binding.ivPhoto.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200L)
                    .start()
            }
        }

        companion object {
            fun create(parent: ViewGroup): ProductImageSliderThumbViewHolder {
                val binding = ItemImageViewerThumbBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ProductImageSliderThumbViewHolder(binding)
            }
        }
    }
}
