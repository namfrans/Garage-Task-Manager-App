package com.example.valentinesgaragetaskmanagementapp.activities.Employee

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.widget.Toast
import com.example.valentinesgaragetaskmanagementapp.R
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.ReportsActivity
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.SettingsActivity
import com.example.valentinesgaragetaskmanagementapp.activities.SignInActivity
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.TasksActivity
import com.example.valentinesgaragetaskmanagementapp.databinding.ActivityEmployeesBinding
import com.example.valentinesgaragetaskmanagementapp.utilities.Constants
import com.example.valentinesgaragetaskmanagementapp.utilities.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class EmployeesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmployeesBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employees)

        binding = ActivityEmployeesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        preferenceManager = PreferenceManager(applicationContext)

        //NAVIGATION
        //Initialize
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        //Set home selector
        bottomNavigationView.selectedItemId = R.id.employeesActivity
        //setOnClick listeners
        bottomNavigationView.setOnItemSelectedListener() { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.empTasksActivity -> {
                    startActivity(Intent(applicationContext, EmpTasksActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.employeesActivity ->{
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
            }
            false
        }
        loadUserDetails()
        getToken()
        setListeners()
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun setListeners() {
        binding.imageSignOut.setOnClickListener {
            signOut()
        }
    }

    private fun loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME))
        binding.textEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL))
        binding.textDepartment.setText(preferenceManager.getString(Constants.KEY_DEPARTMENT))
        binding.textRole.setText(preferenceManager.getString(Constants.KEY_ROLE))
        val bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes!!.size)
        binding.imageProfile.setImageBitmap(bitmap)
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            updateToken(token)
        }
    }

    private fun updateToken(token: String) {
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(Constants.KEY_USER_ID)!!
        )
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnFailureListener {
                showToast("Unable to update token")
            }
    }

    private fun signOut() {
        showToast("Signing out...")
        val database = FirebaseFirestore.getInstance()
        val documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
            preferenceManager.getString(Constants.KEY_USER_ID)!!
        )
        val updates = HashMap<String, Any>()
        updates[Constants.KEY_FCM_TOKEN] = FieldValue.delete()
        documentReference.update(updates)
            .addOnSuccessListener {
                preferenceManager.clear()
                startActivity(Intent(applicationContext, SignInActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                showToast("Unable to connect")
            }
    }
}