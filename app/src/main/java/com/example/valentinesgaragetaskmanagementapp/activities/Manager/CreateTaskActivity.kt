package com.example.valentinesgaragetaskmanagementapp.activities.Manager


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.valentinesgaragetaskmanagementapp.R

import com.example.valentinesgaragetaskmanagementapp.databinding.ActivityCreateTaskBinding
import com.example.valentinesgaragetaskmanagementapp.models.User
import com.example.valentinesgaragetaskmanagementapp.utilities.Constants
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class CreateTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateTaskBinding
    var employeeList: MutableList<String> = ArrayList<String>()
    val users: MutableList<User> = ArrayList<User>()
    private lateinit var image:String
    lateinit var selected: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setListeners()
        getUsers()
    }

    private fun setListeners(){
        binding.cancelBtn.setOnClickListener{
            onBackPressed()
        }
        binding.saveBtn.setOnClickListener{
            if (isValidTaskDetails()) {
                addTaskToFirebase()
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun addTaskToFirebase(){
        loading(true)
        val db = FirebaseFirestore.getInstance()
        val task = HashMap<String, Any>()
        task.put(Constants.KEY_TASK, binding.task.text.toString())
        task.put(Constants.KEY_DESCRIPTION, binding.description.text.toString())
        task.put(Constants.KEY_RECEIVER, binding.assignedTo.selectedItem.toString())
        for (user in users){
            if (user.name.equals(binding.assignedTo.selectedItem.toString())){
                image = user.image
            }
        }
        task.put(Constants.KEY_RECEIVER_IMG, image)
        db.collection(Constants.KEY_COLLECTION_TASK)
            .add(task)
            .addOnSuccessListener { documentReference ->
                loading(false)
                val intent = Intent(applicationContext, ManagerHomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{ e ->
                loading(false)
                showToast(e.message.toString())
            }
    }
    private fun isValidTaskDetails(): Boolean {
        if (binding.task.toString().trim().isEmpty()){
            showToast("Enter a task.")
            return false
        } else if (binding.description.text.toString().trim().isEmpty()){
            showToast("Specify the task description.")
            return false
        }  else if (binding.assignedTo.selectedItem.toString().trim().isEmpty()){
            showToast("Select who is responsible for the task")
            return false
        }else{
            return true
        }
    }
    private fun getUsers(){
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.KEY_COLLECTION_USERS)
            .whereNotEqualTo("role", "Manager")
            .get()
            .addOnSuccessListener {
                    for (queryDocumentSnapshot in it) {
                        if (it.isEmpty) {
                            showToast("There's no users in document!")
                        }
                        val user = User()
                        user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME).toString()
                        user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL).toString()
                        user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE).toString()
                        user.department = queryDocumentSnapshot.getString(Constants.KEY_DEPARTMENT).toString()
                        user.role = queryDocumentSnapshot.getString(Constants.KEY_ROLE).toString()
                        user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN).toString()
                        user.id = queryDocumentSnapshot.id
                        users.add(user)
                        employeeList.add(user.name)
                        showToast("Added: "+user.name)
                    }
            }
            .addOnCompleteListener{
                var empList = employeeList.toList()
                val empAdapter = ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, empList)
                binding.assignedTo.adapter = empAdapter
                binding.assignedTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        adapterView?.getItemAtPosition(position).toString()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
            }
            .addOnFailureListener {
                showToast(it.message.toString())
            }
    }
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.saveBtn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.saveBtn.visibility = View.VISIBLE
        }
    }

}