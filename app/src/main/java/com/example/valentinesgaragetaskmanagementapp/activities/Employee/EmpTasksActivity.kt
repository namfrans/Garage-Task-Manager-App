package com.example.valentinesgaragetaskmanagementapp.activities.Employee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.valentinesgaragetaskmanagementapp.R
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.ManagerHomeActivity
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.ReportsActivity
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.SettingsActivity
import com.example.valentinesgaragetaskmanagementapp.adapters.TasksAdapter
import com.example.valentinesgaragetaskmanagementapp.databinding.ActivityEmpTasksBinding
import com.example.valentinesgaragetaskmanagementapp.databinding.ActivityTasksBinding
import com.example.valentinesgaragetaskmanagementapp.models.Task
import com.example.valentinesgaragetaskmanagementapp.utilities.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.*

class EmpTasksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmpTasksBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskArrayList: ArrayList<Task>
    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emp_tasks)
        binding = ActivityEmpTasksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        preferenceManager = PreferenceManager(applicationContext)
        recyclerView = findViewById(R.id.usersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        taskArrayList = arrayListOf()
        tasksAdapter = TasksAdapter(taskArrayList)
        recyclerView.adapter = tasksAdapter
        EventChangeListener()

        //Initialize
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        //Set home selector
        bottomNavigationView.selectedItemId = R.id.empTasksActivity
        //setOnClick listeners
        bottomNavigationView.setOnItemSelectedListener() { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.empTasksActivity -> {
                    return@setOnItemSelectedListener true
                }
                R.id.empReportsActivity -> {
                    startActivity(Intent(applicationContext, EmpReportsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.emp_settings -> {
                    startActivity(Intent(applicationContext, EmpSettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.employeesActivity ->{
                    startActivity(Intent(applicationContext, EmployeesActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun EventChangeListener() {
        loading(true)
        db = FirebaseFirestore.getInstance()
        db.collection("tasks").orderBy("task", Query.Direction.ASCENDING).
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?,
            ) {
                if (error != null){
                    showToast(error.message.toString())
                    return
                }
                for (doc: DocumentChange in value?.documentChanges!!){
                    if (doc.type == DocumentChange.Type.ADDED){
                        loading(false)
                        taskArrayList.add(doc.document.toObject(Task::class.java))
                    }
                }
                tasksAdapter.notifyDataSetChanged()
            }

        })
    }

    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
}