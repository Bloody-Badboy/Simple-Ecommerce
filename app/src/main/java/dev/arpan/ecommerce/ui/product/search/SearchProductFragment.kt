package dev.arpan.ecommerce.ui.product.search

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.arpan.ecommerce.databinding.FragmentSearchProductBinding
import dev.arpan.ecommerce.ui.NavigationDestinationFragment
import dev.arpan.ecommerce.ui.home.HomeFragmentDirections
import dev.arpan.ecommerce.ui.product.common.ProductListAdapter
import dev.arpan.ecommerce.utils.dismissKeyboard
import dev.arpan.ecommerce.utils.showKeyboard

@AndroidEntryPoint
class SearchProductFragment : NavigationDestinationFragment() {

    companion object {
        private const val WAITING_TIME = 300L
    }

    private val viewModel: SearchProductViewModel by viewModels()
    private var _binding: FragmentSearchProductBinding? = null
    private val binding: FragmentSearchProductBinding
        get() = requireNotNull(_binding)
    private val myHandler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchProductBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initAdapter()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    this@apply.dismissKeyboard()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    myHandler.removeCallbacksAndMessages(null)
                    myHandler.postDelayed(
                        {
                            viewModel.onSearchQueryChanged(newText)
                        },
                        WAITING_TIME
                    )
                    return true
                }
            })

            setOnQueryTextFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    view.findFocus().showKeyboard()
                }
            }
            requestFocus()
        }
    }

    override fun onPause() {
        binding.searchView.dismissKeyboard()
        super.onPause()
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
}