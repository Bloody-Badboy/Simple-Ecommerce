package dev.arpan.ecommerce.ui.filter

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.arpan.ecommerce.data.model.Filter
import dev.arpan.ecommerce.data.model.FilterType
import dev.arpan.ecommerce.databinding.FragmentFilterBinding
import dev.arpan.ecommerce.ui.NavigationDestinationFragment
import dev.arpan.ecommerce.ui.common.FilterNameAdapter
import dev.arpan.ecommerce.ui.common.FilterNameItem
import dev.arpan.ecommerce.ui.common.MultipleChoiceListAdapter
import dev.arpan.ecommerce.ui.common.SingleChoiceListAdapter
import timber.log.Timber

@AndroidEntryPoint
class FilterFragment : NavigationDestinationFragment() {

    private val viewModel: FilterViewModel by viewModels()
    private var _binding: FragmentFilterBinding? = null
    private val binding: FragmentFilterBinding
        get() = requireNotNull(_binding)
    private val args: FilterFragmentArgs by navArgs()

    private val filterNameAdapter = FilterNameAdapter()
    private var filters: List<Filter> = emptyList()

    private val selectedFilters = SparseArray<List<Int>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }
        binding.btnApply.setOnClickListener { Timber.d("$selectedFilters") }

        filterNameAdapter.onFilterSelected = { position ->
            updateFilterNameList(position)
            updateFilterOptionsList(position, filters[position].filterType)
        }

        binding.rvName.adapter = filterNameAdapter
        binding.rvName.itemAnimator = null

        viewModel.filters.observe(viewLifecycleOwner, {
            filters = it
            for (i in it.indices) {
                selectedFilters.put(i, emptyList())
            }
            if (it.isNotEmpty()) {
                updateFilterNameList(0)
                updateFilterOptionsList(0, it[0].filterType)
            }
        })

        viewModel.fetchCategoryFilters(args.category)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateFilterNameList(filterNameIndex: Int) {
        filterNameAdapter.submitList(filters.mapIndexed { index, filter ->
            FilterNameItem(
                text = filter.filterNameValue.name,
                inSelected = index == filterNameIndex,
                isFilterApplied = selectedFilters[index].isNotEmpty()
            )
        })
    }

    private fun updateFilterOptionsList(filterNameIndex: Int, filterType: FilterType) {
        when (filterType) {
            is FilterType.SingleChoice -> {
                binding.rvOptions.adapter = SingleChoiceListAdapter { position ->
                    selectedFilters.put(
                        filterNameIndex, if (position < 0) emptyList() else listOf(position)
                    )
                    updateFilterNameList(filterNameIndex)
                }.apply {
                    data = filterType.options.map { it.name }
                    if (selectedFilters[filterNameIndex].isNotEmpty()) {
                        selectedPosition = selectedFilters[filterNameIndex][0]
                    }
                }
            }
            is FilterType.MultipleChoice -> {
                binding.rvOptions.adapter =
                    MultipleChoiceListAdapter { positions ->
                        selectedFilters.put(filterNameIndex, positions.toList())
                        updateFilterNameList(filterNameIndex)
                    }.apply {
                        data = filterType.options.map { it.name }
                        defaultSelections = selectedFilters[filterNameIndex]
                    }
            }
        }
    }
}