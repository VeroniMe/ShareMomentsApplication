package com.example.sharemomentsapplication.activities

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sharemomentsapplication.R
import com.example.sharemomentsapplication.databinding.ActivityReportBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import models.Report
import models.User
import utilities.Constants
import utilities.UserManager
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private lateinit var menteeName: String
    private var mentorName: String = ""
    private lateinit var mentorPhoneNumber: String
    private lateinit var menteePhoneNumber: String
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var reportsRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDBConnection()

        val bundle: Bundle? = intent.extras
        mentorPhoneNumber = UserManager.getInstance().getUser().phone
        mentorName = UserManager.getInstance().getUser().name + " " +
                UserManager.getInstance().getUser().surname
        binding.addReportETMentorNameTxt.setText(mentorName)

        menteePhoneNumber =  bundle?.getString("menteePhoneNumber") ?: ""
        menteeName = bundle?.getString("menteeName") ?: ""
        binding.addReportETMenteeNameTxt.setText(menteeName)



        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.addReportETDate.setOnClickListener {
            // Create DatePickerDialog
            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.addReportETDate.setText(selectedDate)
            }, year, month, day)
            datePicker.show()
        }

        binding.addMenteeBTNSend.setOnClickListener {
            saveReportToDB()
        }

    }

    private fun getUserName() {
        userRef.child(mentorPhoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val uName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val uSurname =
                        snapshot.child("surname").getValue(String::class.java) ?: "Unknown"
                    mentorName = "${uName} ${uSurname}"

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Data Error", "Failed to read value.", error.toException())
                }
            })
    }

    private fun saveReportToDB() {

        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-LLL-yyyy-HH-mm-ss"))

        val newReport = Report
            .Builder()
            .mentorName(mentorName)
            .menteeName(menteeName)
            .reportDate(formattedDate)
            .description(binding.addReportETDetailsTxt.text.toString())
            .build()

        val reportName = buildString {
            append("report_")
            append(formattedDate)
        }


        reportsRef.child(mentorPhoneNumber).child(menteePhoneNumber).child(reportName).setValue(newReport)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Data saved successfully
                    Log.d("Firebase", "Report saved successfully")
                    Toast.makeText(this, "דיווח חדש נוסף בהצלחה!", Toast.LENGTH_LONG).show()

                } else {
                    // Log the error
                    Log.e("Firebase", "Error saving report", task.exception)
                }
            }
    }

    private fun initDBConnection() {
        database = Firebase.database
        userRef = database.getReference(Constants.Path.RT_FIREBASE_USERS_PATH)
        reportsRef = database.getReference(Constants.Path.RT_FIREBASE_REPORTS_PATH)
    }
}