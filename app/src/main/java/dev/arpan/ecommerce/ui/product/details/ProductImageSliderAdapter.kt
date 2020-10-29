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

package dev.arpan.ecommerce.ui.product.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import dev.arpan.ecommerce.bindImageUrl
import dev.arpan.ecommerce.databinding.ItemProductImageSliderBinding

class ProductImageSliderAdapter :
    PagerAdapter() {
    var imageUrls: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClick: ((position: Int) -> Unit)? = null

    override fun getCount() = imageUrls.size

    override fun isViewFromObject(view: View, obj: Any) = (obj as? View) == view

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return ItemProductImageSliderBinding.inflate(
            LayoutInflater.from(container.context),
            container,
            false
        ).apply {
            root.setOnClickListener {
                onItemClick?.invoke(position)
            }
            ivPhoto.bindImageUrl(imageUrls[position])

            container.addView(root, 0)
        }.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        (obj as? View)?.let {
            container.removeView(it)
        }
    }
}
