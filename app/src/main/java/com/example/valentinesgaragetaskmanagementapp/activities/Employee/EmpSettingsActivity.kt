package com.example.valentinesgaragetaskmanagementapp.activities.Employee

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.example.valentinesgaragetaskmanagementapp.R
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.ManagerHomeActivity
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.ReportsActivity
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.TasksActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class EmpSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.emp_settings_activity)

        //Initialize
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        //Set home selector
        bottomNavigationView.selectedItemId = R.id.emp_settings
        //setOnClick listeners
        bottomNavigationView.setOnItemSelectedListener() { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.empTasksActivity -> {
                    startActivity(Intent(applicationContext, EmpTasksActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.empReportsActivity -> {
                    startActivity(Intent(applicationContext, EmpReportsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.employeesActivity ->{
                    startActivity(Intent(applicationContext, EmployeesActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.emp_settings -> {
                    return@setOnItemSelectedListener true
                }
            }
            false
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.emp_settings, EmpSettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class EmpSettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.emp_root_preferences, rootKey)
        }
    }
}