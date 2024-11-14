package com.example.sharemomentsapplication.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharemomentsapplication.adapters.MenteeAdapter
import com.example.sharemomentsapplication.databinding.ActivityMyMenteesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import interfaces.MenteeCallback
import models.Mentee
import utilities.Constants
import utilities.UserManager

class MyMenteesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyMenteesBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menteesRef: DatabaseReference
    private lateinit var menteeAdapter: MenteeAdapter
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyMenteesBinding.inflate(layoutInflater)
        setContentView(binding.root)



        menteeAdapter = MenteeAdapter(emptyList())
        menteeAdapter.menteeCallback = object : MenteeCallback {
            override fun addReportClicked(mentee: Mentee, position: Int) {

                getUserName { name ->
                    val intent = Intent(this@MyMenteesActivity, ReportActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("menteeName", "${mentee.name} ${mentee.surname}")
                    bundle.putString("menteePhoneNumber", mentee.phone)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    Log.d("MyMenteesActivity", "Go to ReportActivity with mentee: $mentee")
                }

            }

        }
        initDBConnection()

        menteesRef.addListenerForSingleValueEvent(object :
            ValueEventListener { // For realtime data fetching from DB
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val menteesList = mutableListOf<Mentee>()
                for (menteeSnapshot in dataSnapshot.children) {
                    // Deserialize each mentee object into your Mentee class
                    val mentee = menteeSnapshot.getValue(Mentee::class.java)
                    mentee?.let {
                        if(mentee.phoneNumberOfMentor == FirebaseAuth.getInstance().currentUser?.phoneNumber)
                            menteesList.add(it)
                    }
                }

                if (menteesList.size != 0) {
                    menteeAdapter.mentees = menteesList
                    menteeAdapter.notifyDataSetChanged()
                }
                else {
                    menteeAdapter.mentees = emptyList()
                    val builder = AlertDialog.Builder(this@MyMenteesActivity)
                    builder.setTitle("לא נמצאו חיילים")
                    builder.setMessage("נראה שבינתיים אין לך חיילים")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    builder.show()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Data Error", "Failed to read value. (MyMenteesActivity)", error.toException())
            }
        })

        binding.menteesRVList.adapter = menteeAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.menteesRVList.layoutManager = linearLayoutManager

    }

    private fun initDBConnection(){
        database = Firebase.database
        menteesRef = database.getReference(Constants.Path.RT_FIREBASE_MENTEES_PATH)
        userRef = database.getReference(Constants.Path.RT_FIREBASE_USERS_PATH)
    }
    //option to get user details from Firebase
    private fun getUserNameFromDB(onMentorNameRetrieved: (String) -> Unit) {
        userRef.child(FirebaseAuth.getInstance().currentUser?.phoneNumber.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val uName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                    val uSurname =
                        snapshot.child("surname").getValue(String::class.java) ?: "Unknown"
                    val mentorName = "${uName} ${uSurname}"
                    onMentorNameRetrieved(mentorName)

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Data Error", "Failed to read value.", error.toException())
                }
            })
    }

    private fun getUserName(onMentorNameRetrieved: (String) -> Unit) {

        onMentorNameRetrieved(UserManager.getInstance().getUser().name + " " +
                            UserManager.getInstance().getUser().surname)

    }
}


