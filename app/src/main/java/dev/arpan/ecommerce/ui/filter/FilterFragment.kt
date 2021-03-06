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

package dev.arpan.ecommerce.ui.filter

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.arpan.ecommerce.R
import dev.arpan.ecommerce.data.model.Filter
import dev.arpan.ecommerce.data.model.FilterName
import dev.arpan.ecommerce.data.model.FilterOption
import dev.arpan.ecommerce.data.model.FilterType
import dev.arpan.ecommerce.databinding.FragmentFilterBinding
import dev.arpan.ecommerce.ui.NavigationDestinationFragment
import dev.arpan.ecommerce.ui.common.FilterNameAdapter
import dev.arpan.ecommerce.ui.common.FilterNameItem
import dev.arpan.ecommerce.ui.common.MultipleChoiceListAdapter
import dev.arpan.ecommerce.ui.common.SingleChoiceListAdapter

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
    private val isFilterSelected: Boolean
        get() {
            var selected = false
            for (i in 0 until selectedFilters.size()) {
                if (selectedFilters[i].isNotEmpty()) {
                    selected = true
                    break
                }
            }
            return selected
        }
    private var fromClearFilters = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }
        binding.btnApply.setOnClickListener {
            val selectedFiltersMap = mutableMapOf<FilterName, List<FilterOption>>().apply {
                for (i in 0 until selectedFilters.size()) {
                    val options = mutableListOf<FilterOption>()
                    selectedFilters[i].forEach { position ->
                        val filterTypeOptions = when (val filterType = filters[i].filterType) {
                            is FilterType.SingleChoice -> {
                                filterType.options
                            }
                            is FilterType.MultipleChoice -> {
                                filterType.options
                            }
                            else -> {
                                throw IllegalArgumentException("Invalid FilterType: $filterType")
                            }
                        }
                        options.add(filterTypeOptions[position])
                    }
                    put(filters[i].filterName, options)
                }
            }

            viewModel.setAppliedFilterForCategory(
                category = args.category,
                filterMap = selectedFiltersMap
            )
            findNavController().navigateUp()
        }

        filterNameAdapter.onFilterSelected = { position ->
            updateFilterNameList(position)
            updateFilterOptionsList(position, filters[position].filterType)
        }

        binding.rvName.adapter = filterNameAdapter
        binding.rvName.itemAnimator = null

        viewModel.filters.observe(
            viewLifecycleOwner,
            {
                filters = it
                for (i in it.indices) {
                    selectedFilters.put(i, emptyList())
                }

                if (!fromClearFilters) {
                    fromClearFilters = true
                    mapAppliedFilterMapToIndices()
                }
                updateMenuClearFiltersState()

                if (it.isNotEmpty()) {
                    updateFilterNameList(0)
                    updateFilterOptionsList(0, it[0].filterType)
                }
            }
        )

        viewModel.fetchCategoryFilters(args.category)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.toolbar) {
            inflateMenu(R.menu.menu_filter)
            menu.findItem(R.id.action_clear_filters).isEnabled = false
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_clear_filters) {
                    fromClearFilters = true
                    viewModel.fetchCategoryFilters(args.category)
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun mapAppliedFilterMapToIndices() {
        val appliedMap = viewModel.getAppliedFilterForCategory(args.category)
        appliedMap.iterator().forEach { entry ->
            val filterNameIndex = filters.indexOfFirst { filter ->
                filter.filterName == entry.key
            }
            if (filterNameIndex >= 0) {
                val positions = mutableListOf<Int>()
                entry.value.forEach {
                    val options = when (val filterType = filters[filterNameIndex].filterType) {
                        is FilterType.SingleChoice -> {
                            filterType.options
                        }
                        is FilterType.MultipleChoice -> {
                            filterType.options
                        }
                        else -> {
                            throw IllegalArgumentException("Invalid FilterType: $filterType")
                        }
                    }
                    val index = options.indexOfFirst { option -> option == it }
                    if (index >= 0) {
                        positions.add(index)
                    }
                }
                selectedFilters.put(filterNameIndex, positions)
            }
        }
    }

    private fun updateFilterNameList(filterNameIndex: Int) {
        filterNameAdapter.submitList(
            filters.mapIndexed { index, filter ->
                FilterNameItem(
                    text = filter.filterName.name,
                    inSelected = index == filterNameIndex,
                    isFilterApplied = selectedFilters[index].isNotEmpty()
                )
            }
        )
    }

    private fun updateMenuClearFiltersState() {
        binding.toolbar.menu.findItem(R.id.action_clear_filters).isEnabled = isFilterSelected
    }

    private fun updateFilterOptionsList(filterNameIndex: Int, filterType: FilterType) {
        when (filterType) {
            is FilterType.SingleChoice -> {
                binding.rvOptions.adapter = SingleChoiceListAdapter { position ->
                    selectedFilters.put(
                        filterNameIndex, if (position < 0) emptyList() else listOf(position)
                    )
                    updateFilterNameList(filterNameIndex)
                    updateMenuClearFiltersState()
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
                        updateMenuClearFiltersState()
                    }.apply {
                        data = filterType.options.map { it.name }
                        defaultSelections = selectedFilters[filterNameIndex]
                    }
            }
        }
    }
}
