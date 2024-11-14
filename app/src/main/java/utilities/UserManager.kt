package utilities

import android.util.Log
import android.widget.Toast
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

class UserManager private constructor(private var user: User) {

    companion object {

        @Volatile
        private var instance: UserManager? = null
        private lateinit var usersRef: DatabaseReference
        private lateinit var database: FirebaseDatabase

        fun init(user: User, callback:UserDetailsFetchCallback): UserManager {

            return instance ?: synchronized(this) {
                instance ?: UserManager(user).also { 
                    Log.d("init UserManger", "init ")
                    instance = it
                    it.fetchUserData(callback)
                }
            }
        }

        fun getInstance(): UserManager {
            return instance
                ?: throw IllegalStateException("User must sign in/create profile.")
        }
        fun clearInstance() {
            instance = null
            Log.d("UserManager", "UserManager instance is set to null")
        }
    }

    private fun fetchUserData(callback : UserDetailsFetchCallback) {
        database = Firebase.database
        usersRef = database.getReference(Constants.Path.RT_FIREBASE_USERS_PATH)
        usersRef.child(FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // User exists
                    val user = snapshot.getValue(User::class.java)
                    getInstance().updateUser(user as User)
                    callback.onUserDetailsFetched()
                    //user?.let {
                    //    UserManager.init(it)
                    //    Toast.makeText(this@SigningUpActivity, "${user.name} ${user.surname}", Toast.LENGTH_SHORT).show()
                    //}
                } else {
                    Log.d("FirebaseAuth", "User does not authenticated")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
                //Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getUser(): User {
        return user
    }

    fun updateUser(newUser: User) {
        user = newUser
    }


}