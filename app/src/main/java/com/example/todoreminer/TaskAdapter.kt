package com.example.todoreminer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onTaskCompleted: (Task, Boolean) -> Unit,
    private val onTaskLongClicked: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskCheckbox: CheckBox = itemView.findViewById(R.id.task_checkbox)
        val taskTitle: TextView = itemView.findViewById(R.id.task_title)

        init {
            itemView.setOnLongClickListener {
                onTaskLongClicked(tasks[adapterPosition])
                true
            }
            taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onTaskCompleted(tasks[adapterPosition], isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.taskTitle.text = currentTask.title
        holder.taskCheckbox.isChecked = currentTask.isCompleted
    }

    override fun getItemCount() = tasks.size
}