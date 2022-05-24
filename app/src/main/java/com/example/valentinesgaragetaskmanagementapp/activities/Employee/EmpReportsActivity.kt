package com.example.valentinesgaragetaskmanagementapp.activities.Employee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.valentinesgaragetaskmanagementapp.R
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.SettingsActivity
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.TasksActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class EmpReportsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emp_reports)

        //Initialize
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        //Set home selector
        bottomNavigationView.selectedItemId = R.id.empReportsActivity
        //setOnClick listeners
        bottomNavigationView.setOnItemSelectedListener() { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.empTasksActivity -> {
                    startActivity(Intent(applicationContext, EmpTasksActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.empReportsActivity-> {
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
}