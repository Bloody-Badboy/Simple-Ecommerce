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

package dev.arpan.ecommerce.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import dev.arpan.ecommerce.R
import kotlin.properties.Delegates

typealias OnSelectItemListener = (position: Int) -> Unit

class SingleChoiceListAdapter(
    private val checkable: Boolean = true,
    private val itemListener: OnSelectItemListener? = null
) :
    RecyclerView.Adapter<SingleChoiceListAdapter.SingleChoiceItemViewHolder>() {

    private var isReselected = false

    var data: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedPosition by Delegates.observable(-1) { _, oldPos, newPos ->
        if (newPos in data.indices) {
            if (oldPos == newPos && !isReselected) {
                isReselected = true
                notifyItemChanged(oldPos)
            } else {
                isReselected = false
                notifyItemChanged(oldPos)
                notifyItemChanged(newPos)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChoiceItemViewHolder {
        return SingleChoiceItemViewHolder.create(
            if (checkable)
                R.layout.item_simple_list_checkable
            else
                R.layout.item_simple_list,
            parent
        )
    }

    override fun onBindViewHolder(holder: SingleChoiceItemViewHolder, position: Int) {
        holder.bind(data[position], position == selectedPosition && !isReselected)
        holder.itemView.setOnClickListener {
            selectedPosition = position
            if (isReselected) {
                itemListener?.invoke(-1)
            } else {
                itemListener?.invoke(position)
            }
        }
    }

    override fun getItemCount() = data.size

    class SingleChoiceItemViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        fun bind(s: String, selected: Boolean) {
            (view as? TextView)?.apply {
                text = s
                isSelected = selected
            }
            (view as? CheckedTextView)?.apply {
                isChecked = selected
            }
        }

        companion object {
            fun create(@LayoutRes layout: Int, parent: ViewGroup): SingleChoiceItemViewHolder {
                return SingleChoiceItemViewHolder(
                    view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
                )
            }
        }
    }
}
