package com.example.valentinesgaragetaskmanagementapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.valentinesgaragetaskmanagementapp.models.User
import com.example.valentinesgaragetaskmanagementapp.utilities.Constants
import com.example.valentinesgaragetaskmanagementapp.utilities.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


class UsersActivity : AppCompatActivity() {
    private lateinit var preferenceManager: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager = PreferenceManager(applicationContext)
        getUsers()
    }
    private fun getUsers() {
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USERS)
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                val currentUserId = preferenceManager.getString(Constants.KEY_USER_ID)
                if (task.isSuccessful && task.result != null) {
                    val users: MutableList<User> = ArrayList()
                    for (queryDocumentSnapshot in task.result!!) {
                        if (currentUserId == queryDocumentSnapshot.id) {
                            continue
                        }
                        val user:User? = null
                        user!!.name = queryDocumentSnapshot.getString(Constants.KEY_NAME).toString()
                        user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL).toString()
                        user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE).toString()
                        user.department = queryDocumentSnapshot.getString(Constants.KEY_DEPARTMENT).toString()
                        user.role = queryDocumentSnapshot.getString(Constants.KEY_ROLE).toString()
                        user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN).toString()
                        user.id = queryDocumentSnapshot.id
                        users.add(user)
                    }
                }
            }
    }
}