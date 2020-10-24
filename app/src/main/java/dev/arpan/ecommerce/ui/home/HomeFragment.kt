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

package dev.arpan.ecommerce.ui.home

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.arpan.ecommerce.R
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.databinding.FragmentHomeBinding
import dev.arpan.ecommerce.ui.NavigationDestinationFragment
import dev.arpan.ecommerce.ui.drawable.CountDrawable
import dev.arpan.ecommerce.ui.home.HomeFragmentDirections.Companion.toCart
import dev.arpan.ecommerce.ui.home.HomeFragmentDirections.Companion.toProductFilter
import dev.arpan.ecommerce.ui.home.HomeFragmentDirections.Companion.toProductSearch
import dev.arpan.ecommerce.ui.product.list.ProductListFragment
import dev.arpan.ecommerce.utils.onTabSelected
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : NavigationDestinationFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding)
    private var selectedCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initViewPager()

        binding.btnSort.setOnClickListener {
            selectedCategory?.let { category ->
                val sortDialog = SortBottomSheetDialogFragment.newInstance(category)
                sortDialog.onSortOrderSelected = { sortBy ->
                    viewModel.setSortByOrder(category, sortBy)
                }
                sortDialog.show(childFragmentManager, SortBottomSheetDialogFragment.TAG)
            }
        }

        binding.btnFilter.setOnClickListener {
            selectedCategory?.let {
                findNavController().navigate(toProductFilter(it))
            }
        }

        viewModel.appliedFilterCount.observe(
            viewLifecycleOwner,
            {
                Timber.d("$selectedCategory -> $it")
            }
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.run {
            inflateMenu(R.menu.menu_home)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_cart -> {
                        findNavController().navigate(toCart())
                        true
                    }
                    R.id.action_product_search -> {
                        findNavController().navigate(toProductSearch())
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }

        viewModel.cartItemCount.observe(
            viewLifecycleOwner,
            {
                updateCartItemCount(it)
            }
        )
    }

    private fun updateCartItemCount(count: Int) {
        val menuItem: MenuItem = binding.toolbar.menu.findItem(R.id.action_cart)
        with(menuItem.icon as LayerDrawable) {
            val drawable = findDrawableByLayerId(R.id.ic_cart_item_count)
            val badge = (drawable as? CountDrawable) ?: CountDrawable(requireContext())
            badge.count = count
            mutate()
            setDrawableByLayerId(R.id.ic_cart_item_count, badge)
        }
    }

    private fun initViewPager() {
        val homeAdapter = HomeAdapter(childFragmentManager)
        with(binding.viewpager) {
            pageMargin = resources.getDimensionPixelSize(R.dimen.viewpager_page_margin)
            setPageMarginDrawable(R.drawable.page_margin)
            adapter = homeAdapter
            binding.tabs.setupWithViewPager(this)
        }

        viewModel.productCategories.observe(
            viewLifecycleOwner,
            { categories ->
                homeAdapter.categories = categories
                if (viewModel.currentPageIndex in categories.indices) {
                    binding.viewpager.currentItem = viewModel.currentPageIndex
                }
            }
        )

        binding.tabs.onTabSelected { tab ->
            homeAdapter.categories[tab.position].value.let { category ->
                selectedCategory = category
                viewModel.selectedCategory(category)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.currentPageIndex = binding.viewpager.currentItem
    }

    inner class HomeAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        var categories: List<ProductCategory> = emptyList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getCount(): Int = categories.size

        override fun getItem(position: Int) =
            ProductListFragment.newInstance(categories[position].value)

        override fun getPageTitle(position: Int) = categories[position].name
    }
}
