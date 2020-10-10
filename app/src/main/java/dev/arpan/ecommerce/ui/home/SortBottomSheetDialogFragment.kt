package dev.arpan.ecommerce.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.arpan.ecommerce.data.model.SortBy
import dev.arpan.ecommerce.databinding.FragmentHomeSortSheetBinding
import dev.arpan.ecommerce.ui.SingleChoiceListAdapter

typealias OnSortOrderSelected = (SortBy) -> Unit

class SortBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "sort_bottom_sheet_dialog"

        fun newInstance() = SortBottomSheetDialogFragment()
    }

    private var _binding: FragmentHomeSortSheetBinding? = null
    private val binding: FragmentHomeSortSheetBinding
        get() = requireNotNull(_binding)

    var sortOrderSelected: OnSortOrderSelected? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeSortSheetBinding.inflate(inflater, container, false)

        binding.ibClose.setOnClickListener { dismiss() }

        binding.rvOptions.adapter = SingleChoiceListAdapter { position ->
            if (position in SortBy.values().indices) {
                val sortBy = SortBy.values()[position]
                sortOrderSelected?.invoke(sortBy)
            }
            dismiss()
        }.apply {
            data = SortBy.values().map { it.displayName }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}