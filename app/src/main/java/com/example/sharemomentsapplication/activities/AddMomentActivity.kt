package com.example.sharemomentsapplication.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.sharemomentsapplication.databinding.ActivityAddMomentBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import models.Moment
import utilities.Constants
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddMomentActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAddMomentBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var momentsRef: DatabaseReference
    private lateinit var usersRef : DatabaseReference

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var imagesRef: StorageReference
    var uri : Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityAddMomentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDBConnections()
        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
            binding.addedImage.setImageURI(it)
            if(it != null) {
                uri = it
            }
        }
        binding.momentBTNChoosePhoto.setOnClickListener{
            pickImage.launch("image/*")
        }
        binding.momentBTNAddMoment.setOnClickListener{
            uploadMoment()
        }
        storageRef = FirebaseStorage.getInstance().getReference("images")
    }

    private fun uploadMoment() {
        uri?.let {
            Moment.incrementCounter()
            val fileName = buildString {
                append(FirebaseAuth.getInstance().currentUser?.phoneNumber.toString())
                append("_")
                append(Moment.getMomentId())
            }


            imagesRef.child(fileName).putFile(it)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            val imgURL = url.toString()
                            storeMomentDescription(imgURL)
                        }
                }


            
        }
        //TESTS - for test purpose, link to the site for picture
        //val url =  "https://cdn.pixabay.com/photo/2023/10/14/09/20/mountains-8314422_1280.png"
        storeMomentDescription(" ")
    }

    private fun storeMomentDescription(url: String) {


        val currentUserPhone = FirebaseAuth.getInstance().currentUser?.phoneNumber
        if (currentUserPhone == null) {
            Log.w("Data Error", "Phone number not found for current user. (AddMomentActivity)")
            return
        }
        usersRef.child(currentUserPhone).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val name = dataSnapshot.child("name").getValue(String::class.java)?:"Unknown"
                val surname = dataSnapshot.child("surname").getValue(String::class.java)?:"Unknown"
                val currentDate = LocalDate.now()
                val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                val newMoment = Moment
                    .Builder()
                    .momentName(Moment.getMomentId())
                    .momentPhotoUrl(url)
                    .volunteerName(
                        buildString {
                            append(name)
                            append(" ")
                            append(surname)
                        }
                    )
                    .description(binding.momentTXTDdescription.text.toString())
                    .creationDate(formattedDate)
                    .likesCount(0)
                    .build()


                momentsRef.child(newMoment.momentName).setValue(newMoment)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Data saved successfully
                            Log.d("Firebase", "Moment saved successfully (AddMomentActivity) ")
                            Toast.makeText(this@AddMomentActivity, "חוויה חדשה נוספה בהצלחה!", Toast.LENGTH_LONG).show()

                            val momentsCount = database.getReference(Constants.Path.RT_FIREBASE_MOMENTS_COUNT_PATH)
                            momentsCount.setValue(Moment.getCounter())

                        } else {
                            // Log the error
                            Log.e("Firebase", "Error saving moment (AddMomentActivity)", task.exception)
                        }
                    }

            }
            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Data Error", "Failed to read value (AddMomentActivity).", error.toException())
            }
        })

    }

    private fun checkMomentCounter() {
         if(Moment.getCounter() == 0) {
             val momentsCount = database.getReference(Constants.Path.RT_FIREBASE_MOMENTS_COUNT_PATH)
             momentsCount.addListenerForSingleValueEvent(object : ValueEventListener {
                 override fun onDataChange(snapshot: DataSnapshot) {
                     val count = snapshot.getValue(Int::class.java)
                     Moment.setCounter(count as Int)
                 }

                 override fun onCancelled(error: DatabaseError) {
                     TODO("Not yet implemented")
                 }

             })
         }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)

    }

    private fun initDBConnections() {
        database = Firebase.database
        storage = Firebase.storage
        storageRef = storage.reference
        /**********************************************************/
        // Create a child reference
        // imagesRef now points to "images"
        imagesRef = storageRef.child(Constants.Path.STORAGE_MOMENTS_IMAGES_PATH)
        momentsRef = database.getReference(Constants.Path.RT_FIREBASE_MOMENTS_PATH)
        momentsRef.get().addOnSuccessListener { dataSnapshot ->
            val childCount = dataSnapshot.childrenCount
            Moment.setCounter(childCount.toInt())
            Log.d("Firebase", "Number of children: $childCount (AddMomentActivity)")
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Failed to count children (AddMomentActivity)", e)
        }
        usersRef = database.getReference(Constants.Path.RT_FIREBASE_USERS_PATH)


    }
}

