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
import com.example.sharemomentsapplication.databinding.ActivityAddMenteeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import models.Mentee
import utilities.Constants

class AddMenteeActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAddMenteeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menteesRef: DatabaseReference

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var imagesRef: StorageReference
    var uri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenteeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = Firebase.database
        menteesRef = database.getReference("mentees")
        storage = Firebase.storage
        storageRef = storage.reference
        imagesRef = storageRef.child(Constants.Path.STORAGE_PROFILE_PHOTOS_PATH)
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            binding.addedImage.setImageURI(it)
            if(it != null) {
                uri = it
            }
        }
        binding.addMenteeBTNChoosePhoto.setOnClickListener{
            pickImage.launch("image/*")
        }
        initViews()
    }

    private fun initViews() {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.addMenteeETBirthday.setOnClickListener {
            // Create DatePickerDialog
            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.addMenteeETBirthday.setText(selectedDate)

            }, year, month, day)
            datePicker.show()
        }

        binding.addMenteeETServiceStart.setOnClickListener {
            // Create DatePickerDialog
            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.addMenteeETServiceStart.setText(selectedDate)

            }, year, month, day)
            datePicker.show()
        }

        binding.addMenteeETServiceEnd.setOnClickListener {
            // Create DatePickerDialog
            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.addMenteeETServiceEnd.setText(selectedDate)
            }, year, month, day)
            datePicker.show()
        }

        binding.addMenteeBTNSend.setOnClickListener {
            if(allFieldsFilled()) {
                storeProfilePhoto()
            } else {
                Toast.makeText(this, "חסרים פרטים!", Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun storeProfilePhoto() {
        uri?.let {
            val fileName = binding.addMenteeETPhone.text.toString()

            imagesRef.child(fileName).putFile(it)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            val imgURL = url.toString()
                            saveMenteeToDB(imgURL)
                        }
                }
        }
        saveMenteeToDB("")
    }

    private fun saveMenteeToDB(imgURL: String) {

        val currentUserPhone = FirebaseAuth.getInstance().currentUser?.phoneNumber
        if (currentUserPhone == null) {
            Log.w("Data Error", "Phone number not found for current user.")
            return
        }

        val prefix = binding.addMenteeTIPhone.prefixText.toString().trim()
        val phoneNumberInput = binding.addMenteeETPhone.text.toString().trim()
        val menteeNum = if (prefix.isNotEmpty()) "$prefix$phoneNumberInput" else phoneNumberInput
        val newMentee = Mentee
            .Builder()
            .name(binding.addMenteeETName.text.toString())
            .surname(binding.addMenteeETSurname.text.toString())
            .phone(menteeNum)
            .serviceStart(binding.addMenteeETServiceStart.text.toString())
            .serviceEnd(binding.addMenteeETServiceEnd.text.toString())
            .birthday(binding.addMenteeETBirthday.text.toString())
            .city(binding.addMenteeETCity.text.toString())
            .homeland(binding.addMenteeETHomeland.text.toString())
            .phoneNumberOfMentor(currentUserPhone)
            .profilePhotoUrl(imgURL)
            .build()



        menteesRef.child(newMentee.phone).setValue(newMentee)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Data saved successfully
                    Log.d("Firebase", "Mentee saved successfully")
                    Toast.makeText(this, "חניך חדש נוסף בהצלחה!", Toast.LENGTH_LONG).show()
                    transactToFeedScreen()
                } else {
                    // Log the error
                    Log.e("Firebase", "Error saving new mentee", task.exception)
                }
            }
    }

    private fun transactToFeedScreen() {
        val intent = Intent(this, FeedActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun allFieldsFilled(): Boolean {
        var isValid = true

        if (binding.addMenteeETPhone.text.toString().trim().isEmpty()) {
            binding.addMenteeETPhone.error = "חסר מספר טלפון"
            isValid = false
        }

        if (binding.addMenteeETName.text.toString().trim().isEmpty()) {
            binding.addMenteeETName.error = "חסר שם"
            isValid = false
        }

        if (binding.addMenteeETSurname.text.toString().trim().isEmpty()) {
            binding.addMenteeETSurname.error = "חסר שם משפחה"
            isValid = false
        }
        if (binding.addMenteeETServiceStart.text.toString().trim().isEmpty()) {
            binding.addMenteeETServiceStart.error = "חסר תאריך גיוס"
            isValid = false
        }

        if (binding.addMenteeETServiceEnd.text.toString().trim().isEmpty()) {
            binding.addMenteeETServiceEnd.error = "חסר תאריך שחרור"
            isValid = false
        }

        if (binding.addMenteeETBirthday.text.toString().trim().isEmpty()) {
            binding.addMenteeETBirthday.error = "חסר תאריך לידה"
            isValid = false
        }
        if (binding.addMenteeETCity.text.toString().trim().isEmpty()) {
            binding.addMenteeETCity.error = "חסרה עיר מגורים"
            isValid = false
        }
        if (binding.addMenteeETHomeland.text.toString().trim().isEmpty()) {
            binding.addMenteeETHomeland.error = "חסרה מדינת מקור"
            isValid = false
        }
        return isValid
    }

}