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
import dagger.hilt.android.AndroidEntryPoint
import dev.arpan.ecommerce.R
import dev.arpan.ecommerce.data.model.ProductCategory
import dev.arpan.ecommerce.databinding.FragmentHomeBinding
import dev.arpan.ecommerce.ui.NavigationDestinationFragment
import dev.arpan.ecommerce.ui.drawable.CountDrawable
import dev.arpan.ecommerce.ui.home.HomeFragmentDirections.Companion.toCart
import dev.arpan.ecommerce.ui.home.HomeFragmentDirections.Companion.toNavProductSearch
import dev.arpan.ecommerce.ui.product.list.ProductListFragment

@AndroidEntryPoint
class HomeFragment : NavigationDestinationFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initViewPager()

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
                        findNavController().navigate(toNavProductSearch())
                        true
                    }
                    else -> {
                        false
                    }
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

    private fun initViewPager() {
        val homeAdapter = HomeAdapter(childFragmentManager)
        with(binding.viewpager) {
            pageMargin = resources.getDimensionPixelSize(R.dimen.viewpager_page_margin)
            setPageMarginDrawable(R.drawable.page_margin)
            adapter = homeAdapter
            binding.tabs.setupWithViewPager(this)
        }

        viewModel.productCategories.observe(viewLifecycleOwner, {
            homeAdapter.categories = it
        })
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
            ProductListFragment.newInstance(categories[position].endPoint)

        override fun getPageTitle(position: Int) = categories[position].name
    }
}
