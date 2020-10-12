package dev.arpan.ecommerce.ui.common

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import dev.arpan.ecommerce.R

typealias OnSelectionChange = (positions: List<Int>) -> Unit

class MultipleChoiceListAdapter(private val listener: OnSelectionChange) :
    RecyclerView.Adapter<MultipleChoiceListAdapter.MultipleChoiceItemViewHolder>() {

    private val selections = SparseBooleanArray()
    private val selectedPositions: List<Int>
        get() {
            return mutableListOf<Int>().apply {
                for (i in 0 until selections.size()) {
                    if (selections.valueAt(i)) {
                        add(selections.keyAt(i))
                    }
                }
            }
        }

    var defaultSelections: List<Int> = emptyList()
        set(value) {
            field = value
            value.forEach { position ->
                if (position in data.indices) {
                    selections.put(position, true)
                    notifyItemChanged(position)
                }
            }
        }

    var data: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultipleChoiceItemViewHolder {
        return MultipleChoiceItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MultipleChoiceItemViewHolder, position: Int) {
        (holder.itemView as? CheckedTextView)?.apply {
            text = data[position]
            isChecked = selections.get(position)

            setOnClickListener {
                selections.put(position, !isChecked)
                notifyItemChanged(position)
                listener.invoke(selectedPositions)
            }
        }
    }

    override fun getItemCount() = data.size

    class MultipleChoiceItemViewHolder private constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

        companion object {

            fun create(parent: ViewGroup): MultipleChoiceItemViewHolder {
                return MultipleChoiceItemViewHolder(
                    view = LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.item_simple_list_checkable,
                            parent, false
                        )
                )
            }
        }
    }
}