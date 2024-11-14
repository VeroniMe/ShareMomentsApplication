package com.example.sharemomentsapplication.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sharemomentsapplication.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import interfaces.UserDetailsFetchCallback
import models.User
import utilities.Constants
import utilities.UserManager

class VerificationActivity : AppCompatActivity() {


    private lateinit var auth : FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var storedVerificationId : String
    private lateinit var verify_BTN_verify : MaterialButton
    private lateinit var id_otp : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        val bundle : Bundle? = intent.extras
        storedVerificationId = bundle?.getString("verificationId")!!
        auth = FirebaseAuth.getInstance()
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        verify_BTN_verify = findViewById(R.id.verify_BTN_verify)
        id_otp = findViewById(R.id.id_otp)
        verify_BTN_verify.setOnClickListener {

            val otp = id_otp.text.toString().trim()
            if(!otp.isEmpty()) {
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredentials(credential)

            } else {
                Toast.makeText(this, "תקליד/י מספר שקיבלת ב-SMS", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun signInWithPhoneAuthCredentials(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful) {

                    ifUserExistCheck(FirebaseAuth.getInstance().currentUser?.phoneNumber.toString())

                } else {
                    if(task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "קוד שהכנסת הינו שגוי", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun ifUserExistCheck(userPhone: String) {
        database = Firebase.database
        usersRef = database.getReference(Constants.Path.RT_FIREBASE_USERS_PATH)
        usersRef.child(userPhone).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // User exists
                    val user = snapshot.getValue(User::class.java)
                    //user?.let {
                        UserManager.init(user!!, object : UserDetailsFetchCallback {
                            override fun onUserDetailsFetched() {
                                // Callback after data is fetched, navigate to the next activity

                                val intent = Intent(this@VerificationActivity, FeedActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        })
                } else {
                    // User does not exist
                    Toast.makeText(this@VerificationActivity, "נראה שאתה פעם ראשונה אצלינו!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@VerificationActivity, UserInfoActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
                Log.e("Error", "Error in VerificationActivity: ${error.message}")
            }
        })
    }


}