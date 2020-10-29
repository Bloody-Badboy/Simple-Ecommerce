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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import dev.arpan.ecommerce.bindImages
import dev.arpan.ecommerce.databinding.FragmentImageViewerBinding
import dev.arpan.ecommerce.ui.NavigationDestinationFragment
import dev.arpan.ecommerce.ui.product.details.ProductImageSliderAdapter

class ImageViewerFragment : NavigationDestinationFragment() {

    private var _binding: FragmentImageViewerBinding? = null
    private val binding: FragmentImageViewerBinding
        get() = requireNotNull(_binding)
    private val args: ImageViewerFragmentArgs by navArgs()
    private var isThumbRecycleViewHidden = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageViewerBinding.inflate(inflater, container, false)

        binding.pagerProductImages.bindImages(args.imageUrls.toList(), null)
        (binding.pagerProductImages.adapter as? ProductImageSliderAdapter)?.let {
            it.onItemClick = { _ ->
                val translateValue =
                    if (isThumbRecycleViewHidden) 0f else binding.rvThumbs.height.toFloat()
                binding.rvThumbs.animate().translationY(translateValue)
                    .setDuration(200L).start()

                isThumbRecycleViewHidden = !isThumbRecycleViewHidden
            }
        }
        binding.pagerProductImages.setCurrentItem(args.position, true)

        binding.rvThumbs.adapter = ImageViewerThumbAdapter {
            binding.pagerProductImages.setCurrentItem(it, true)
        }.apply {
            images = args.imageUrls.toList()
            selectedPosition = args.position
        }
        binding.rvThumbs.itemAnimator = null
        binding.rvThumbs.smoothScrollToPosition(args.position)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
