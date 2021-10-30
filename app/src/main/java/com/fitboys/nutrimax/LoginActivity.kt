package com.fitboys.nutrimax

import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast

import android.content.Intent

import android.text.TextUtils
import android.util.Log

import com.google.firebase.auth.FirebaseAuth

import android.widget.Button
import android.widget.EditText

import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

        private var mAuth: FirebaseAuth? = null

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
                        Toast.makeText(
                            this@LoginActivity,
                            "User logged in successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
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