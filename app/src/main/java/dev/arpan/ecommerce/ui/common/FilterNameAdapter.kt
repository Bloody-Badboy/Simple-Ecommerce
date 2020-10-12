package dev.arpan.ecommerce.ui.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.arpan.ecommerce.databinding.ItemFilterNameBinding

typealias OnFilterSelected = (position: Int) -> Unit

data class FilterNameItem(
    val text: String,
    var inSelected: Boolean,
    var isFilterApplied: Boolean
)

class FilterNameAdapter() :
    ListAdapter<FilterNameItem, FilterNameAdapter.FilterNameViewHolder>(COMPARATOR) {

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<FilterNameItem>() {
            override fun areItemsTheSame(
                oldItem: FilterNameItem,
                newItem: FilterNameItem
            ) = oldItem == newItem

            override fun areContentsTheSame(
                oldItem: FilterNameItem,
                newItem: FilterNameItem
            ) = oldItem == newItem
        }
    }

    var onFilterSelected: OnFilterSelected? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterNameViewHolder {
        return FilterNameViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: FilterNameViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onFilterSelected?.invoke(position)
        }
    }

    class FilterNameViewHolder(private val binding: ItemFilterNameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(filterName: FilterNameItem) {
            binding.tvFilterName.text = filterName.text
            binding.root.isSelected = filterName.inSelected
            binding.viewSelectedMark.isVisible = filterName.isFilterApplied
        }

        companion object {
            fun create(parent: ViewGroup): FilterNameViewHolder {
                return FilterNameViewHolder(
                    ItemFilterNameBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}

