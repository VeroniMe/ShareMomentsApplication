package com.example.sharemomentsapplication.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.sharemomentsapplication.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import models.Mentee
import models.User
import utilities.Constants
import utilities.ImageLoader
import utilities.UserManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var databaseF : FirebaseDatabase
    private lateinit var usersRef : DatabaseReference
    private lateinit var menteesRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseF = Firebase.database
        usersRef = databaseF.getReference(Constants.Path.RT_FIREBASE_USERS_PATH)
        menteesRef = databaseF.getReference(Constants.Path.RT_FIREBASE_MENTEES_PATH)


        setDataToProfile()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    //function that help to initilize user profile from Firebase
    private fun setDataToProfile_from_database() {

        val currentUserPhone = FirebaseAuth.getInstance().currentUser?.phoneNumber
        if (currentUserPhone == null) {
            Log.w("Data Error", "Phone number not found for current user. (ProfileActivity)")
            return
        }
        usersRef.child(currentUserPhone).addListenerForSingleValueEvent(object : ValueEventListener { // For one time data fetching from DB
                // For realtime data fetching from DB

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val ds = dataSnapshot.child("name")
                    val uname = dataSnapshot.child("name").getValue(String::class.java)?:"Unknown"
                    val usurname = dataSnapshot.child("surname").getValue(String::class.java)?:"Unknown"
                    val uemail = dataSnapshot.child("email").getValue(String::class.java)?:"Unknown"

                    val startDateString = dataSnapshot.child("startDate").getValue(String::class.java)?:"Unknown"

                    val user = User.Builder()
                        .name(uname)
                        .surname(usurname)
                        .email(uemail)
                        .phone(currentUserPhone)
                        .startDate(startDateString)
                        .mentees(dataSnapshot.child(Constants.Path.RT_FIREBASE_MENTEES_PATH).children.mapNotNull {
                            menteeSnapshot ->
                                menteeSnapshot.getValue(Mentee::class.java)
                        })
                        .build()

                    //Log.d("Data", "Value is: $value")

                    binding.profileLBLName.text = buildString {
                        append(user.name)
                        append(" ")
                        append(user.surname)
                    }
                    binding.profileTXTPhone.text = user.phone

                    binding.profileTXTEmail.text = user.email
                    binding.profileTXTDays.text =
                        ChronoUnit.DAYS.between(LocalDate.parse(user.startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalDate.now()).toString()


                //binding.mainLBLGreeting.text = buildString {
                    //    append(value)
                    //}
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("Data Error", "Failed to read value.", error.toException())
                }
            })

        menteesRef
            .orderByChild("phoneNumberOfMentor")
            .equalTo(currentUserPhone)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val menteeList = mutableListOf<Mentee>()
                    for (menteeSnapshot in snapshot.children) {
                        val mentee = menteeSnapshot.getValue(Mentee::class.java)
                        if (mentee != null) {
                            menteeList.add(mentee)
                        }
                    }
                    binding.profileTXTMenteesNum.text = menteeList.size.toString()

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Log.e("FirebaseError", "Error fetching mentees: ${error.message}")
                }
            })

    }


    private fun setDataToProfile() {

        binding.profileLBLName.text = buildString {
            append(UserManager.getInstance().getUser().name)
            append(" ")
            append(UserManager.getInstance().getUser().surname)
        }
        binding.profileTXTPhone.text = UserManager.getInstance().getUser().phone
        binding.profileTXTEmail.text = UserManager.getInstance().getUser().email
        binding.profileTXTDays.text =
            ChronoUnit.DAYS.between(LocalDate.parse(UserManager.getInstance().getUser().startDate,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalDate.now()).toString()

        menteesRef
            .orderByChild("phoneNumberOfMentor")
            .equalTo(UserManager.getInstance().getUser().phone)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val menteeList = mutableListOf<Mentee>()
                    for (menteeSnapshot in snapshot.children) {
                        val mentee = menteeSnapshot.getValue(Mentee::class.java)
                        if (mentee != null) {
                            menteeList.add(mentee)
                        }
                    }
                    binding.profileTXTMenteesNum.text = menteeList.size.toString()

                }
                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Log.e("FirebaseError", "Error fetching mentees: ${error.message} (ProfileActivity)")
                }
            })
        ImageLoader.getInstance().load(UserManager.getInstance().getUser().profilePhotoUrl, binding.profileIMGPhoto)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                Log.d("Button Action: ", "Return to previous activity")
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}