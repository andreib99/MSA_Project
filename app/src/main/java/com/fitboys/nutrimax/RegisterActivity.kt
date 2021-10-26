package com.fitboys.nutrimax

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.fitboys.nutrimax.data.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val etRegEmail = findViewById<EditText>(R.id.etRegEmail)
        val etRegUsername = findViewById<EditText>(R.id.etRegUsername)
        val etRegPassword = findViewById<EditText>(R.id.etRegPass)
        val etRegPasswordVerification = findViewById<EditText>(R.id.etRegPass_Verification)
        val tvLoginHere = findViewById<TextView>(R.id.tvLoginHere)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        mAuth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener(View.OnClickListener { view: View? -> createUser(etRegUsername, etRegEmail, etRegPassword, etRegPasswordVerification) })
        tvLoginHere.setOnClickListener(View.OnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
                )
            )
        })
    }

    private fun createUser(etRegUsername: EditText, etRegEmail: EditText, etRegPassword: EditText, etRegPassword_Verification: EditText) {
        val username = Objects.requireNonNull(etRegUsername.text).toString()
        val email = Objects.requireNonNull(etRegEmail.text).toString()
        val password = Objects.requireNonNull(etRegPassword.text).toString()
        val verificationPassword = Objects.requireNonNull(etRegPassword_Verification.text).toString()

        when {
            TextUtils.isEmpty(username) -> {
                etRegUsername.error = "Username cannot be empty"
                etRegUsername.requestFocus()
            }
            TextUtils.isEmpty(email) -> {
                etRegEmail.error = "Email cannot be empty"
                etRegEmail.requestFocus()
            }
            TextUtils.isEmpty(password) -> {
                etRegPassword.error = "Password cannot be empty"
                etRegPassword.requestFocus()
            }
            TextUtils.isEmpty(verificationPassword) -> {
                etRegPassword_Verification.error = "Password cannot be empty"
                etRegPassword_Verification.requestFocus()
            }
            password != verificationPassword -> {
                etRegPassword_Verification.error = "Passwords don't match"
                etRegPassword_Verification.requestFocus()
            }
            else -> {
                mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "User registered successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration Error: " + task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}