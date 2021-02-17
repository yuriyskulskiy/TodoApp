package com.skul.testappgl.presentation.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skul.testappgl.R
import com.skul.testappgl.data.UiItemTask


class TasksRecyclerViewAdapter(private val onClick: (UiItemTask) -> Unit) :
    ListAdapter<UiItemTask, RecyclerView.ViewHolder>(TaskDiffCallback()) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = getItem(position)
        (holder as TaskViewHolder).bind(task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_task_item, parent, false),
            onClick
        )
    }

    class TaskViewHolder(
        view: View,
        val onClick: (UiItemTask) -> Unit,
    ) : RecyclerView.ViewHolder(view) {
        private var titleTV: TextView
        private var completedCheckBox: CheckBox
        private var currentTask: UiItemTask? = null

        init {
            titleTV = view.findViewById(R.id.titleTV)
            completedCheckBox = view.findViewById(R.id.completedChB)

            view.setOnClickListener {
                currentTask?.let {
                    onClick(it)
                }
            }

            completedCheckBox.setOnClickListener {
                currentTask?.let {
                    onClick(it)
                }
            }

        }

        fun bind(item: UiItemTask) {
            currentTask = item
            titleTV.text = item.title
            completedCheckBox.isChecked = item.isCompleted
            applyColor(item.isCompleted, itemView)
        }

        private fun applyColor(isCompleted: Boolean, view: View) {
            val color = if (!isCompleted) {
                Color.parseColor(view.context.getString(R.string.not_completed))
            } else {
                Color.parseColor(view.context.getString(R.string.completed))

            }
            view.setBackgroundColor(color);
        }
    }
}


private class TaskDiffCallback : DiffUtil.ItemCallback<UiItemTask>() {

    override fun areItemsTheSame(oldItem: UiItemTask, newItem: UiItemTask): Boolean {
        return oldItem.uniqueIdentifier == newItem.uniqueIdentifier
    }

    override fun areContentsTheSame(oldItem: UiItemTask, newItem: UiItemTask): Boolean {
        return oldItem == newItem
    }
}