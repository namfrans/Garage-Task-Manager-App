package com.example.valentinesgaragetaskmanagementapp.activities.Manager

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.valentinesgaragetaskmanagementapp.R
import com.example.valentinesgaragetaskmanagementapp.activities.SignInActivity
import com.example.valentinesgaragetaskmanagementapp.databinding.ActivityManagerHomeBinding
import com.example.valentinesgaragetaskmanagementapp.models.User
import com.example.valentinesgaragetaskmanagementapp.utilities.Constants
import com.example.valentinesgaragetaskmanagementapp.utilities.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging

class ManagerHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerHomeBinding
    private lateinit var preferenceManager: PreferenceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_home)

        binding = ActivityManagerHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Initialize
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        //Set home selector
        bottomNavigationView.selectedItemId = R.id.managerHomeActivity
        //setOnClick listeners
        bottomNavigationView.setOnItemSelectedListener() { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.tasksActivity -> {
                    startActivity(Intent(applicationContext, TasksActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.managerHomeActivity -> return@setOnItemSelectedListener true
                R.id.reportsActivity -> {
                    startActivity(Intent(applicationContext, ReportsActivity::class.java))
                    overridePendingTransition(0, 0)
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
        preferenceManager = PreferenceManager(applicationContext)
        loadUserDetails()
        getToken()
        setListeners()
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun setListeners() {
        binding.fabAddTask.setOnClickListener{
            val intent = Intent(applicationContext, CreateTaskActivity::class.java)
            startActivity(intent)
        }
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