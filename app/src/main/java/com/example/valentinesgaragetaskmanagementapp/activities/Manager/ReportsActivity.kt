package com.example.valentinesgaragetaskmanagementapp.activities.Manager

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.valentinesgaragetaskmanagementapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReportsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        //Initialize
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        //Set home selector
        bottomNavigationView.selectedItemId = R.id.reportsActivity
        //setOnClick listeners
        bottomNavigationView.setOnItemSelectedListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.tasksActivity -> {
                    startActivity(Intent(applicationContext, TasksActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.managerHomeActivity -> {
                    startActivity(Intent(applicationContext, ManagerHomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.reportsActivity -> {
                    return@setOnItemSelectedListener true
                }
                R.id.settings -> {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
}