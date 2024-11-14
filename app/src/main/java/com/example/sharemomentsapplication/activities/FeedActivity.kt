package com.example.sharemomentsapplication.activities


import android.app.AlertDialog
import com.example.sharemomentsapplication.adapters.MomentAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharemomentsapplication.R
import com.example.sharemomentsapplication.databinding.ActivityFeedBinding
import com.example.sharemomentsapplication.databinding.NavHeaderBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import interfaces.MomentCallback
import models.Moment
import utilities.Constants
import utilities.ImageLoader
import utilities.UserManager

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var  auth : FirebaseAuth
    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var momentsAdapter: MomentAdapter
    private lateinit var momentsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()

        var currentUser = auth.currentUser
        if(currentUser == null) {
            val intent = Intent(this, SigningUpActivity::class.java)
            startActivity(intent)
            //finish()
        }

        val headerBinding = NavHeaderBinding.bind(binding.navigationView.getHeaderView(0))
        headerBinding.userName.text = getString(R.string.user_name_display,UserManager.getInstance().getUser().name, UserManager.getInstance().getUser().surname)
        ImageLoader.getInstance().load(UserManager.getInstance().getUser().profilePhotoUrl, headerBinding.profileImage)


        momentsAdapter = MomentAdapter(emptyList())
        momentsRef = initDBConnection(Constants.Path.RT_FIREBASE_MOMENTS_PATH)

        momentsAdapter.momentCallback = object : MomentCallback {
            override fun likeClickedCallback(moment: Moment, position: Int) {
                moment.likesCount++
                momentsRef.child(moment.momentName).child("likesCount").setValue(moment.likesCount)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Data saved successfully
                            Log.d("Firebase", "Add likes successfully (FeedActivity)")
                        } else {
                            Log.e("Firebase", "Error while save likes count (FeedActivity)", task.exception)
                        }
                    }
            }

        }
        momentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val momentsList = mutableListOf<Moment>()
                for (momentSnapshot in dataSnapshot.children) {

                    val moment = momentSnapshot.getValue(Moment::class.java)
                    moment?.let {
                         momentsList.add(it)
                    }
                }

                if (momentsList.size != 0) {
                    momentsAdapter.moments = momentsList
                    momentsAdapter.notifyDataSetChanged()
                }
                else {
                    momentsAdapter.moments = emptyList()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Data Error", "Failed to read moments values. (FeedActivity)", error.toException())
            }

        })

        binding.feedRVList.adapter = momentsAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.feedRVList.layoutManager = linearLayoutManager




        initilizeMenuItems()
        //For Menu
        binding.feedBTNMenu.setOnClickListener {
            if(binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.END)
            }
        }


    }

    private fun initDBConnection(path: String): DatabaseReference {
        database = Firebase.database
        return database.getReference(path)
    }

    private fun initilizeMenuItems() {
        val drawerLayout : DrawerLayout = binding.drawerLayout
        val navView : NavigationView = binding.navigationView
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()



        navView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.nav_profile -> {
                    // Handle the click for Item 1
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    //return true
                    //finish()
                }

                R.id.nav_my_mentees -> {
                    // Handle the click for Item 2
                    val intent = Intent(this, MyMenteesActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_add_mentee -> {
                    val intent = Intent(this, AddMenteeActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_add_moment -> {
                    val intent = Intent(this, AddMomentActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_log_out -> {
                    auth.signOut()
                    UserManager.clearInstance()
                    val intent = Intent(this, SigningUpActivity::class.java)
                    startActivity(intent)


                }

            }
            true

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.drawer_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }



}