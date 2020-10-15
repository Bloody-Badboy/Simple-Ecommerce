package dev.arpan.ecommerce.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.arpan.ecommerce.data.model.SortBy
import dev.arpan.ecommerce.databinding.FragmentHomeSortSheetBinding
import dev.arpan.ecommerce.ui.common.SingleChoiceListAdapter

typealias OnSortOrderSelected = (SortBy) -> Unit

class SortBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "sort_bottom_sheet_dialog"
        private const val EXTRA_CATEGORY = "category"

        fun newInstance(category: String) = SortBottomSheetDialogFragment().apply {
            arguments = bundleOf(EXTRA_CATEGORY to category)
        }
    }

    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })
    private var _binding: FragmentHomeSortSheetBinding? = null
    private val binding: FragmentHomeSortSheetBinding
        get() = requireNotNull(_binding)

    private lateinit var category: String

    var onSortOrderSelected: OnSortOrderSelected? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        category = requireNotNull(arguments?.getString(EXTRA_CATEGORY))
    }

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

        val selectedSortBy = viewModel.getSortByOrder(category)
        singleChoiceListAdapter.selectedPosition = options.indexOf(selectedSortBy)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}