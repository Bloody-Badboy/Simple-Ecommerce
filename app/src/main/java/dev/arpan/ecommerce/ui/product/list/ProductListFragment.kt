package dev.arpan.ecommerce.ui.product.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.arpan.ecommerce.databinding.FragmentProductListBinding
import dev.arpan.ecommerce.ui.home.HomeFragmentDirections
import dev.arpan.ecommerce.ui.product.common.ProductListAdapter

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    companion object {
        private const val EXTRA_CATEGORY = "category"

        fun newInstance(category: String) = ProductListFragment().apply {
            arguments = bundleOf(
                EXTRA_CATEGORY to category
            )
        }
    }

    private val viewModel: ProductListViewModel by viewModels()
    private var _binding: FragmentProductListBinding? = null
    private val binding: FragmentProductListBinding
        get() = requireNotNull(_binding)
    private lateinit var category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = requireNotNull(arguments?.getString(EXTRA_CATEGORY))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initAdapter()

        viewModel.sortBy.observe(viewLifecycleOwner, { sortBy ->
            if (viewModel.previousSortByOrder != sortBy) {
                viewModel.previousSortByOrder = sortBy
                viewModel.fetchProducts(category, sortBy = sortBy)
            }
        })

        viewModel.appliedFilterMap.observe(viewLifecycleOwner, { map ->
            if (viewModel.previousAppliedFilterMap != map) {
                viewModel.previousAppliedFilterMap = map
                viewModel.fetchProducts(category, filterMap = map)
            }
        })

        if (viewModel.products.value == null) {
            viewModel.fetchProducts(category)
        }
        viewModel.observeChangesForCategory(category)
        return binding.root
    }

    private fun initAdapter() {
        val productListAdapter = ProductListAdapter { productId ->
            findNavController().navigate(
                HomeFragmentDirections.toNavProductDetails(
                    productId
                )
            )
        }
        with(binding.rvProducts) {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
            adapter = productListAdapter
        }

        viewModel.products.observe(
            viewLifecycleOwner,
            {
                productListAdapter.submitList(it) {
                    val layoutManager = (binding.rvProducts.layoutManager as GridLayoutManager)
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (position != RecyclerView.NO_POSITION) {
                        binding.rvProducts.scrollToPosition(position)
                    }
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
