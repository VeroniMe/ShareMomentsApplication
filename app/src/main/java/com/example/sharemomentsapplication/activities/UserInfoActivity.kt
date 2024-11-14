package com.example.sharemomentsapplication.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sharemomentsapplication.databinding.ActivityUserInfoBinding

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import interfaces.UserDetailsFetchCallback
import models.User
import utilities.Constants
import utilities.UserManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserInfoActivity : AppCompatActivity(), UserDetailsFetchCallback {

    private lateinit var binding : ActivityUserInfoBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var usersRef : DatabaseReference

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var imagesRef: StorageReference
    var uri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = Firebase.database
        storage = Firebase.storage
        storageRef = storage.reference
        imagesRef = storageRef.child(Constants.Path.STORAGE_PROFILE_PHOTOS_PATH)
        usersRef = database.getReference("users")

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            binding.addedImage.setImageURI(it)
            if(it != null) {
                uri = it
            }
        }
        binding.newUserBTNChoosePhoto.setOnClickListener{
            pickImage.launch("image/*")
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.startEditText.setOnClickListener {
            // Create DatePickerDialog
            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                binding.startEditText.setText(formattedDate)


            }, year, month, day)
            datePicker.show()
        }

        binding.saveButton.setOnClickListener{
            if(allFieldsFilled()) {
                storeProfilePhoto()
            } else {
                Toast.makeText(this, "חסרים פרטים!", Toast.LENGTH_LONG).show()
            }
        }


    }


    private fun storeProfilePhoto() {
        uri?.let {
            val fileName = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

            imagesRef.child(fileName).putFile(it)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            val imgURL = url.toString()
                            saveNewUserToDB(imgURL)
                        }
                }
        }
        saveNewUserToDB("")
    }

    private fun saveNewUserToDB(imgURL: String) {

        val newUser = User
            .Builder()
            .name(binding.nameEditText.text.toString())
            .surname(binding.surnameEditText.text.toString())
            .email(binding.emailEditText.text.toString())
            .phone(FirebaseAuth.getInstance().currentUser?.phoneNumber.toString())
            .startDate(binding.startEditText.text.toString())
            .mentees(emptyList())
            .profilePhotoUrl(imgURL)
            .build()


        usersRef.child(newUser.phone).setValue(newUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    UserManager.init(newUser, this)
                    // Data saved successfully
                    Log.d("Firebase", "User saved successfully (UserInfoActivity)")
                    Toast.makeText(this, "נרשמת בהצלחה!", Toast.LENGTH_LONG).show()
                    //transactToFeedScreen()
                } else {
                    // Log the error
                    Log.e("Firebase", "Error saving user (UserInfoActivity) ", task.exception)
                }
            }
    }



    private fun transactToFeedScreen() {
        val intent = Intent(this, FeedActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onUserDetailsFetched() {
        val intent = Intent(this, FeedActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun allFieldsFilled(): Boolean {
        var isValid = true

        if (binding.nameEditText.text.toString().trim().isEmpty()) {
            binding.nameEditText.error = "חסר שם"
            isValid = false
        }

        if (binding.surnameEditText.text.toString().trim().isEmpty()) {
            binding.surnameEditText.error = "חסר שם משפחה"
            isValid = false
        }
        if (binding.emailEditText.text.toString().trim().isEmpty()) {
            binding.emailEditText.error = "חסרה כתובת אלקטרונית"
            isValid = false
        }
        if (binding.startEditText.text.toString().trim().isEmpty()) {
            binding.startEditText.error = "חסר תאריך תחילת ההתנדבות"
            isValid = false
        }
        return isValid
    }
}