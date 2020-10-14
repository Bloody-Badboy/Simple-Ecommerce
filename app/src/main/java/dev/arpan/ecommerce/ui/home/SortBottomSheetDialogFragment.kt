package dev.arpan.ecommerce.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.arpan.ecommerce.data.model.SortBy
import dev.arpan.ecommerce.databinding.FragmentHomeSortSheetBinding
import dev.arpan.ecommerce.ui.common.SingleChoiceListAdapter

typealias OnSortOrderSelected = (SortBy) -> Unit

class SortBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "sort_bottom_sheet_dialog"

        fun newInstance() = SortBottomSheetDialogFragment()
    }

    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })
    private var _binding: FragmentHomeSortSheetBinding? = null
    private val binding: FragmentHomeSortSheetBinding
        get() = requireNotNull(_binding)

    var onSortOrderSelected: OnSortOrderSelected? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeSortSheetBinding.inflate(inflater, container, false)

        binding.ibClose.setOnClickListener { dismiss() }

        val options = SortBy.values()

        val singleChoiceListAdapter = SingleChoiceListAdapter { position ->
            if (position in options.indices) {
                onSortOrderSelected?.invoke(options[position])
            }
            dismiss()
        }
        binding.rvOptions.adapter = singleChoiceListAdapter
        singleChoiceListAdapter.data = options.map { it.displayName }

        viewModel.sortBy.observe(viewLifecycleOwner, {
            singleChoiceListAdapter.selectedPosition = options.indexOf(it)
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}