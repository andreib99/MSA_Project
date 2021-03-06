package com.fitboys.nutrimax

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast

import android.content.Intent
import android.os.Build

import android.text.TextUtils
import android.util.Log

import com.google.firebase.auth.FirebaseAuth

import android.widget.Button
import android.widget.EditText

import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class LoginActivity : AppCompatActivity() {

        private var mAuth: FirebaseAuth? = null

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)
            val etLoginEmail = findViewById<EditText>(R.id.etLoginEmail)
            val etLoginPassword = findViewById<EditText>(R.id.etLoginPass)
            val tvRegisterHere = findViewById<TextView>(R.id.tvRegisterHere)
            val btnLogin = findViewById<Button>(R.id.btnLogin)

            mAuth = FirebaseAuth.getInstance()
            val db = Firebase.firestore

            btnLogin.setOnClickListener { view -> loginUser(etLoginEmail, etLoginPassword) }

            tvRegisterHere.setOnClickListener(View.OnClickListener { view: View? ->
                startActivity(
                    Intent(
                        this@LoginActivity,
                        RegisterActivity::class.java
                    )
                )
            })
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun loginUser(etLoginEmail: EditText, etLoginPassword: EditText) {
            val email = etLoginEmail.text.toString()
            val password = etLoginPassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                etLoginEmail.error = "Email cannot be empty"
                etLoginEmail.requestFocus()
            } else if (TextUtils.isEmpty(password)) {
                etLoginPassword.error = "Password cannot be empty"
                etLoginPassword.requestFocus()
            } else {

                mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = mAuth?.currentUser
                        if (user != null) {
                            //check if email is verified
                            if (!user.isEmailVerified) {
                                mAuth!!.currentUser?.sendEmailVerification()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "You need to verify your email!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else
                            {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "User logged in successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                val db = Firebase.firestore
                                mAuth?.currentUser?.uid?.let {
                                    var date = ""
                                    var calories = ""
                                    db.collection("users")
                                        .document(it).get().addOnSuccessListener { document ->
                                            if (document != null)
                                            {
                                                date = document.data?.get("last_activity").toString()
                                                calories = document.data?.get("caloriesIntake").toString()
                                            }
                                            else
                                            {
                                                Log.d(TAG, "No such document")
                                            }

                                            if(date != "")
                                            {
                                                if(date != LocalDateTime.now().format(DateTimeFormatter.ISO_DATE).toString())
                                                {
                                                    db.collection("users")
                                                        .document(it).update(
                                                            mapOf(
                                                                "last_activity" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE),
                                                                "remainingCalories" to calories
                                                            )
                                                        )
                                                        .addOnSuccessListener {
                                                            Log.d(ContentValues.TAG, "DocumentSnapshot updated")
                                                        }
                                                        .addOnFailureListener { e ->
                                                            Log.w(ContentValues.TAG, "Error updating document", e)
                                                        }
                                                }
                                            }
                                            if(calories == "0")
                                            {
                                                startActivity(
                                                    Intent(
                                                        this@LoginActivity,
                                                        CaloriesCalculatorActivity::class.java
                                                    )
                                                )
                                            }
                                            else
                                            {
                                                startActivity(
                                                    Intent(
                                                        this@LoginActivity,
                                                        HomeActivity::class.java
                                                    )
                                                )
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.d(TAG, "get failed with ", exception)
                                        }
                                }
                            }
                        }


                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Log in Error: " + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }