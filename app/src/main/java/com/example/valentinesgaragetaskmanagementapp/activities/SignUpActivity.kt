package com.example.valentinesgaragetaskmanagementapp.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.example.valentinesgaragetaskmanagementapp.R
import com.example.valentinesgaragetaskmanagementapp.activities.Employee.EmployeesActivity
import com.example.valentinesgaragetaskmanagementapp.activities.Manager.ManagerHomeActivity
import com.example.valentinesgaragetaskmanagementapp.databinding.ActivitySignUpBinding
import com.example.valentinesgaragetaskmanagementapp.utilities.Constants
import com.example.valentinesgaragetaskmanagementapp.utilities.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var preferenceManager: PreferenceManager
    private var encodedImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        preferenceManager = PreferenceManager(applicationContext)

        //Drop-down list section
        var selected: String
        val departmentList = listOf("Department","HOD", "Vehicles", "Trailers")
        val roleList = listOf("Role","Manager", "Electrician", "Mechanic")
        val deptAdapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, departmentList)
        val rolesAdapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, roleList)
        binding.spDepartment.adapter = deptAdapter
        binding.spRole.adapter = rolesAdapter

        binding.spDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, id: Long) {
                selected = adapterView.getItemAtPosition(position).toString()
                if (selected == "HOD"){
                    binding.spRole.setSelection(1,true)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.spRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, id: Long) {
                selected = adapterView.getItemAtPosition(position).toString()
                if (binding.spRole.selectedItem.equals("Manager")){
                    binding.spDepartment.setSelection(1,true)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        //Calling methods
        setListeners()
    }

    private fun setListeners(){
        binding.textSignIn.setOnClickListener{
            onBackPressed()
        }
        binding.layoutImage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            chooseImage.launch(intent)
        }
        binding.buttonSignUp.setOnClickListener{
            if (isValidSignUpDetails()) {
                signUp()
            }
        }
    }

    private fun showToast(message: String){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun signUp(){
        loading(true)
        val db = FirebaseFirestore.getInstance()
        val user = HashMap<String, Any>()
        user[Constants.KEY_NAME] = binding.inputName.text.toString()
        user[Constants.KEY_EMAIL] = binding.inputEmail.text.toString()
        user[Constants.KEY_PASSWORD] = binding.inputPassword.text.toString()
        user[Constants.KEY_DEPARTMENT] = binding.spDepartment.selectedItem.toString()
        user[Constants.KEY_ROLE] = binding.spRole.selectedItem.toString()
        user[Constants.KEY_IMAGE] = encodedImage!!
        db.collection(Constants.KEY_COLLECTION_USERS)
            .add(user)
            .addOnSuccessListener { documentReference ->
                loading(false)
                preferenceManager.putBoolean()
                preferenceManager.putString(Constants.KEY_USER_ID, documentReference.id)
                preferenceManager.putString(Constants.KEY_NAME, binding.inputName.text.toString())
                preferenceManager.putString(Constants.KEY_EMAIL, binding.inputEmail.text.toString())
                preferenceManager.putString(Constants.KEY_DEPARTMENT, binding.spDepartment.selectedItem.toString())
                preferenceManager.putString(Constants.KEY_ROLE, binding.spRole.selectedItem.toString())
                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage)
                //Change this later
                if (binding.spDepartment.selectedItem.toString() == "HOD"){
                    val intent = Intent(applicationContext, ManagerHomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }else{
                    val intent = Intent(applicationContext, EmployeesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
            .addOnFailureListener{ e ->
                loading(false)
                showToast(e.message.toString())
            }
    }

    private fun encodeImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private val chooseImage = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null) {
                val imageUri = result.data!!.data
                try {
                    val inputStream = contentResolver.openInputStream(imageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    binding.imageProfile.setImageBitmap(bitmap)
                    binding.textAddImage.visibility = View.GONE
                    encodedImage = encodeImage(bitmap)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isValidSignUpDetails(): Boolean {
        if (this.encodedImage == null){
            showToast("Select your profile picture.")
            return false
        } else if (binding.inputName.text.toString().trim().isEmpty()){
            showToast("Enter your name.")
            return false
        } else if (binding.inputEmail.text.toString().trim().isEmpty()){
            showToast("Enter your email.")
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.text.toString()).matches()){
            showToast("Enter a valid email address.")
            return false
        } else if (binding.inputPassword.text.toString().trim().isEmpty()){
            showToast("Enter your password")
            return false
        }else if (binding.inputConfirmPassword.text.toString().trim().isEmpty()){
            showToast("Confirm your password")
            return false
        }else if (binding.inputPassword.text.toString() != binding.inputConfirmPassword.text.toString()){
            showToast("Your passwords must be the same")
            return false
        }else if(binding.spDepartment.selectedItem.toString() == "Department"){
            showToast("Select your department.")
            return false
        }else if (binding.spRole.selectedItem.toString() == "Role") {
            showToast("Select your role.")
            return false
        }else{
            return true
        }
    }
    private fun loading(isLoading: Boolean) {
        if (isLoading) {
            binding.buttonSignUp.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.buttonSignUp.visibility = View.VISIBLE
        }
    }
}

