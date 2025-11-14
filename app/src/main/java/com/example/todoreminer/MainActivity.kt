package com.example.todoreminer

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var tasks: MutableList<Task>
    private lateinit var taskAdapter: TaskAdapter
    private val playerProfile = PlayerProfile()
    private lateinit var playerStatsTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        playerStatsTextView = findViewById(R.id.player_stats)
        updatePlayerStats()

        val taskRecyclerView: RecyclerView = findViewById(R.id.task_recycler_view)
        taskRecyclerView.layoutManager = LinearLayoutManager(this)

        tasks = mutableListOf(
            Task(1, "Buy groceries", "Milk, eggs, bread", false),
            Task(2, "Finish homework", "Math and Science", true),
            Task(3, "Go for a run", "30 minutes", false)
        )

        taskAdapter = TaskAdapter(tasks, this::onTaskCompleted, this::onTaskLongClicked)
        taskRecyclerView.adapter = taskAdapter

        val addTaskFab: FloatingActionButton = findViewById(R.id.add_task_fab)
        addTaskFab.setOnClickListener {
            showAddTaskDialog()
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                tasks.removeAt(position)
                taskAdapter.notifyItemRemoved(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(taskRecyclerView)
    }

    private fun onTaskCompleted(task: Task, isCompleted: Boolean) {
        task.isCompleted = isCompleted
        if (isCompleted) {
            GamificationManager.completeTask(playerProfile)
            updatePlayerStats()
            Log.d("Gamification", "Level: ${playerProfile.level}, XP: ${playerProfile.experiencePoints}")
        }
    }

    private fun updatePlayerStats() {
        playerStatsTextView.text = "Level: ${playerProfile.level}, XP: ${playerProfile.experiencePoints}"
    }

    private fun onTaskLongClicked(task: Task) {
        showEditTaskDialog(task)
    }

    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val taskTitleInput = dialogView.findViewById<EditText>(R.id.task_title_input)
        val taskDescriptionInput = dialogView.findViewById<EditText>(R.id.task_description_input)
        val setReminderButton = dialogView.findViewById<Button>(R.id.set_reminder_button)
        var selectedTimestamp: Long? = null

        setReminderButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                TimePickerDialog(this, { _, hourOfDay, minute ->
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                    selectedTimestamp = calendar.timeInMillis
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        AlertDialog.Builder(this)
            .setTitle("Add a new task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = taskTitleInput.text.toString()
                val description = taskDescriptionInput.text.toString()
                if (title.isNotEmpty()) {
                    val newTask = Task(System.currentTimeMillis(), title, description, false, selectedTimestamp)
                    tasks.add(newTask)
                    taskAdapter.notifyItemInserted(tasks.size - 1)
                    selectedTimestamp?.let { scheduleAlarm(newTask) }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditTaskDialog(task: Task) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)
        val taskTitleInput = dialogView.findViewById<EditText>(R.id.task_title_input)
        val taskDescriptionInput = dialogView.findViewById<EditText>(R.id.task_description_input)
        val setReminderButton = dialogView.findViewById<Button>(R.id.set_reminder_button)
        var selectedTimestamp = task.reminderTimestamp

        taskTitleInput.setText(task.title)
        taskDescriptionInput.setText(task.description)

        setReminderButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                TimePickerDialog(this, { _, hourOfDay, minute ->
                    calendar.set(year, month, dayOfMonth, hourOfDay, minute)
                    selectedTimestamp = calendar.timeInMillis
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        AlertDialog.Builder(this)
            .setTitle("Edit task")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = taskTitleInput.text.toString()
                val description = taskDescriptionInput.text.toString()
                if (title.isNotEmpty()) {
                    task.title = title
                    task.description = description
                    task.reminderTimestamp = selectedTimestamp
                    taskAdapter.notifyItemChanged(tasks.indexOf(task))
                    selectedTimestamp?.let { scheduleAlarm(task) }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun scheduleAlarm(task: Task) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("TASK_TITLE", task.title)
            putExtra("TASK_DESCRIPTION", task.description)
        }
        val pendingIntent = PendingIntent.getBroadcast(this, task.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        task.reminderTimestamp?.let {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, it, pendingIntent)
        }
    }
}