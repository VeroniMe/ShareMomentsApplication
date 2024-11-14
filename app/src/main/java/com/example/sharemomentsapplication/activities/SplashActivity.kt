package com.example.sharemomentsapplication.activities

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.sharemomentsapplication.R
import com.example.sharemomentsapplication.databinding.ActivitySplashBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String
    private lateinit var phoneNumber: String
    private lateinit var clb: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        clb = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            //called when user is already verified
            override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credentials)
            }

            //when the verifications are failed
            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@SplashActivity, "Failed!", Toast.LENGTH_LONG).show()
                binding.splashLOTTIELottie.cancelAnimation()
                finish()
            }
            //
            override fun onCodeSent(verId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("onCode Sent: ", "onCodeSent:$verId")
                verificationId = verId
                moveToVerificationActivity()

            }
        }


        auth = FirebaseAuth.getInstance()
        val bundle : Bundle? = intent.extras
        phoneNumber = bundle?.getString("userSignUpNumber")?: ""

        startAnimation(binding.splashLOTTIELottie)
        startOTPVerification()


    }

    private fun signInWithPhoneAuthCredential(credentials: PhoneAuthCredential) {
        auth.signInWithCredential(credentials)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, transition to VerificationActivity
                    moveToVerificationActivity()
                } else {
                    // Sign in failed, display error message
                    Toast.makeText(this, "Verification failed.", Toast.LENGTH_SHORT).show()
                    binding.splashLOTTIELottie.cancelAnimation()
                }
            }
    }

    private fun moveToVerificationActivity() {
        // Stop the Lottie animation
        binding.splashLOTTIELottie.cancelAnimation()
        // Move to VerificationActivity, passing the verificationId
        val intent = Intent(this, VerificationActivity::class.java)
        intent.putExtra("verificationId", verificationId)
        startActivity(intent)
        finish()
    }

    private fun startAnimation(lottieAnimationView: LottieAnimationView) {
        lottieAnimationView.resumeAnimation()
        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                Log.d("LottieAnimation", "Animation ended")
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        lottieAnimationView.repeatCount = LottieDrawable.INFINITE
        lottieAnimationView.playAnimation()
    }

    private fun startOTPVerification() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(clb)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun navigateToVerificationActivity() {
        val intent = Intent(this, VerificationActivity::class.java)
        var bundle = Bundle()
        bundle.putString("storedVerificationId", verificationId)
        startActivity(intent)
        finish()
    }
}