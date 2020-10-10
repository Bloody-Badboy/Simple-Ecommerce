package dev.arpan.ecommerce.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

typealias ItemSelectListener = (Int) -> Unit

class SingleChoiceListAdapter(private val listener: ItemSelectListener? = null) :
    RecyclerView.Adapter<SingleChoiceListAdapter.SingleChoiceItemViewHolder>() {

    var data: List<String> = emptyList()
        set(value) {
            field = value
            if (value.isNotEmpty()) {
                selectedPosition = 0
            }
            notifyDataSetChanged()
        }

    private var selectedPosition by Delegates.observable(-1) { _, oldPos, newPos ->
        if (newPos in data.indices) {
            notifyItemChanged(oldPos)
            notifyItemChanged(newPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChoiceItemViewHolder {
        return SingleChoiceItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SingleChoiceItemViewHolder, position: Int) {
        holder.bind(data[position], position == selectedPosition)
        holder.itemView.setOnClickListener {
            selectedPosition = position
            listener?.invoke(position)
        }
    }

    override fun getItemCount() = data.size

    class SingleChoiceItemViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind(s: String, selected: Boolean) {
            (view as? CheckedTextView)?.run {
                text = s
                isChecked = selected
            }
        }

        companion object {
            fun create(parent: ViewGroup): SingleChoiceItemViewHolder {
                return SingleChoiceItemViewHolder(
                    view = LayoutInflater.from(parent.context)
                        .inflate(
                            android.R.layout.simple_list_item_single_choice,
                            parent, false
                        )
                )
            }
        }
    }
}