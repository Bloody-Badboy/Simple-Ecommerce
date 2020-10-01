package dev.arpan.ecommerce.ui.product.details

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import dev.arpan.ecommerce.R
import dev.arpan.ecommerce.databinding.FragmentProductDetailsBinding
import dev.arpan.ecommerce.ui.NavigationDestinationFragment
import dev.arpan.ecommerce.ui.drawable.CountDrawable
import dev.arpan.ecommerce.ui.product.details.ProductDetailsFragmentDirections.Companion.toCart
import dev.arpan.ecommerce.ui.product.list.ProductListAdapter

@AndroidEntryPoint
class ProductDetailsFragment : NavigationDestinationFragment() {

    private val viewModel: ProductDetailsViewModel by viewModels()
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding: FragmentProductDetailsBinding
        get() = requireNotNull(_binding)
    private val args: ProductDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val productListAdapter = ProductListAdapter { productId ->
            findNavController().navigate(
                ProductDetailsFragmentDirections.toNavProductDetails(
                    productId
                )
            )
        }
        with(binding.rvProducts) {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            adapter = productListAdapter
        }

        viewModel.productDetails.observe(viewLifecycleOwner, {
            productListAdapter.submitList(it.suggestedProducts)
        })

        if (viewModel.productDetails.value == null) {
            viewModel.fetchProductDetails(args.productId)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.run {
            inflateMenu(R.menu.menu_product_details)

            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_cart) {
                    findNavController().navigate(toCart())
                    true
                } else {
                    false
                }
            }
        }

        viewModel.cartItemCount.observe(viewLifecycleOwner, {
            updateCartItemCount(it)
        })
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
}
