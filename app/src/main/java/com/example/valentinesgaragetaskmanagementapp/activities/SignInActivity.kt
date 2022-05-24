package com.example.valentinesgaragetaskmanagementapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.valentinesgaragetaskmanagementapp.R
import com.example.valentinesgaragetaskmanagementapp.activities.Employee.EmployeesActivity
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.ManagerHomeActivity
import com.example.valentinesgaragetaskmanagementapp.databinding.ActivitySignInBinding
import com.example.valentinesgaragetaskmanagementapp.utilities.Constants
import com.example.valentinesgaragetaskmanagementapp.utilities.PreferenceManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        preferenceManager = PreferenceManager(applicationContext)
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN) == true){
            //Look it later on and improve
            val intent = Intent(this, ManagerHomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setListeners()
    }

    private fun setListeners() {
        binding.textCreateAccount.setOnClickListener {
            startActivity(Intent(applicationContext, SignUpActivity::class.java))
        }
        binding.buttonSignIn.setOnClickListener{
            if (isValidSignInDetails()){
                signIn()
            }
        }
    }

    fun signIn(){
        loading(true)
        val db = FirebaseFirestore.getInstance()
        db.collection(Constants.KEY_COLLECTION_USERS)
            .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
            .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.text.toString())
            .get()
            .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0
                ) {
                    val documentSnapshot = task.result!!.documents[0]
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true)
                    preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.id)
                    preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME))
                    preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE))
                    preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL))
                    preferenceManager.putString(Constants.KEY_DEPARTMENT, documentSnapshot.getString(Constants.KEY_DEPARTMENT))
                    preferenceManager.putString(Constants.KEY_ROLE, documentSnapshot.getString(Constants.KEY_ROLE))
                    //Look at later on
                    if (documentSnapshot.getString(Constants.KEY_DEPARTMENT).equals("HOD")){
                        val intent = Intent(applicationContext, ManagerHomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }else{
                        val intent = Intent(applicationContext, EmployeesActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                } else {
                    loading(false)
                    showToast("Unable to Sign in")
                }
            }
    }

    private fun loading(isLoading: Boolean){
        if (isLoading) {
            binding.buttonSignIn.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignIn.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidSignInDetails(): Boolean {
        if (binding.inputEmail.text.toString().trim().isEmpty()) {
            showToast("Enter your email.")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()) {
            showToast("Enter a valid email.")
            return false
        } else if (binding.inputPassword.text.toString().trim().isEmpty()) {
            showToast("Enter your password.")
            return false
        } else {
            return true
        }
    }
}