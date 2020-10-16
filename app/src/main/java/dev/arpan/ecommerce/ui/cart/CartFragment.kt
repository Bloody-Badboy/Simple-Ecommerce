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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import dev.arpan.ecommerce.databinding.FragmentCartBinding
import dev.arpan.ecommerce.ui.NavigationDestinationFragment
import dev.arpan.ecommerce.ui.home.HomeFragmentDirections

@AndroidEntryPoint
class CartFragment : NavigationDestinationFragment() {

    private val viewModel: CartViewModel by viewModels()
    private var _binding: FragmentCartBinding? = null
    private val binding: FragmentCartBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initAdapter()

        if (viewModel.products.value == null)
            viewModel.fetchCartProducts()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAdapter() {
        val productListAdapter = CartItemsAdapter(
            onCartItemClick = { productId ->
                findNavController().navigate(
                    HomeFragmentDirections.toNavProductDetails(
                        productId
                    )
                )
            },
            onCartItemRemove = { productId ->
                showRemoveConfirmDialog(productId)
            }
        )

        with(binding.rvProducts) {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = productListAdapter
        }

        viewModel.products.observe(
            viewLifecycleOwner,
            {
                productListAdapter.submitList(it)
            }
        )
    }

    private fun showRemoveConfirmDialog(productId: Long) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Remove Item")
            setMessage("Are you sure to remove the item from cart ?")
            setPositiveButton("Remove") { _, _ ->
                viewModel.removeProductFromCart(productId)
            }
            setNegativeButton("Cancel", null)
        }.show()
    }
}
