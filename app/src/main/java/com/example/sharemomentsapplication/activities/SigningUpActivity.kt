package com.example.sharemomentsapplication.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.sharemomentsapplication.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import interfaces.UserDetailsFetchCallback
import models.User
import utilities.Constants
import utilities.UserManager
import utilities.UserManager.Companion
import utilities.UserManager.Companion.getInstance

class SigningUpActivity : AppCompatActivity(), UserDetailsFetchCallback {


    private lateinit var auth : FirebaseAuth
    private lateinit var sign_up_ET_phone : TextInputEditText

    private lateinit var sign_up_phone : TextInputLayout
    private lateinit var sign_up_BTN_signup : MaterialButton
    private lateinit var progressBar : ProgressBar

    private lateinit var signinCV : CardView

    private lateinit var usersRef: DatabaseReference
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signing_up)

        progressBar = findViewById(R.id.progressBar)
        signinCV = findViewById(R.id.sign_in_CV)
        progressBar.max = 1000
        val currentProgress = 900
        ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
            .setDuration(3000)
            .start()

        auth = FirebaseAuth.getInstance()
        checkCurrentUser()
        sign_up_phone = findViewById(R.id.sign_up_phone)
        sign_up_ET_phone = findViewById(R.id.sign_up_ET_phone)
        sign_up_BTN_signup = findViewById(R.id.sign_up_BTN_signup)
        sign_up_BTN_signup.setOnClickListener{
                Log.d("------------------SIGN UP", "SIGN UP CALLBACK")
                login()
        }

    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            //there can be a state, that user data deleted from DB - need to check
            fetchUserDataTry()
            val user = User()
            UserManager.init(user, this)
        } else {
            signinCV.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE

        }
    }

    private fun login() {

        val prefix = sign_up_phone.prefixText.toString().trim()
        val phoneNumberInput = sign_up_ET_phone.text.toString().trim()
        val userNum = if (prefix.isNotEmpty()) "$prefix$phoneNumberInput" else phoneNumberInput

        if(userNum.isNotEmpty()) {
            val intent = Intent(this, SplashActivity::class.java)
            val bundle = Bundle()
            bundle.putString("userSignUpNumber", userNum)
            intent.putExtras(bundle)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "צריך להוסיף מספר טלפון!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onUserDetailsFetched() {
        val intent = Intent(this, FeedActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
        finish()
    }
    private fun fetchUserDataTry() {
        database = Firebase.database
        usersRef = database.getReference(Constants.Path.RT_FIREBASE_USERS_PATH)
        usersRef.child(FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // User exists
                    val user = snapshot.getValue(User::class.java)
                    getInstance().updateUser(user as User)
                    onUserDetailsFetched()
                } else {
                    Log.d("FirebaseAuth", "User does not authenticated")
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}