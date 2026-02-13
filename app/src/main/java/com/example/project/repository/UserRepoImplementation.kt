package com.example.project.repository

import androidx.compose.animation.core.snap
import com.example.project.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImplementation : UserRepo {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val ref: DatabaseReference = database.getReference("Users")

    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userId = auth.currentUser?.uid
                if (userId != null) {

                    ref.child(userId).child("password").setValue(password)
                }
                callback(true, "Login Successful")
            }
            .addOnFailureListener { exception ->
                val errorMessage = when (exception) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "No account found with this email."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Incorrect password."
                    else -> exception.localizedMessage ?: "An unknown error occurred"
                }
                callback(false, errorMessage)
            }
    }

    override fun forgotPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {

        val cleanEmail = email.trim()

        auth.sendPasswordResetEmail(cleanEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    android.util.Log.d("FIREBASE_DEBUG", "Reset email sent successfully to: $cleanEmail")
                    callback(true, "Reset email sent to $cleanEmail")
                } else {

                    val error = task.exception?.message ?: "Unknown Firebase Error"
                    android.util.Log.e("FIREBASE_DEBUG", "Failed to send email: $error")
                    callback(false, error)
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Registration success", "${auth.currentUser?.uid}")
            } else {
                callback(false, "${it.exception?.message}", "")
            }
        }
    }


    override fun addUserToDatabase(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User Registered successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getUserById(
        userId: String,
        callback: (Boolean, UserModel?) -> Unit
    ) {
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    callback(true, user)
                } else {
                    callback(false, null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, null)
            }
        })
    }



    override fun getAllUser(callback: (Boolean, List<UserModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var allUsers = mutableListOf<UserModel>()
                    for (user in snapshot.children) {
                        val model = user.getValue(UserModel::class.java)
                        if (model != null) {
                            allUsers.add(model)
                        }
                    }
                    callback(true, allUsers)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, emptyList())
            }
        })
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun deleteUser(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "User deleted successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun updateProfile(
        userId: String,
        model: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Profile updated successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }
}



