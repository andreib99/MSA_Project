package com.fitboys.nutrimax

import android.content.Intent

import androidx.core.content.ContextCompat.startActivity

import com.google.firebase.auth.FirebaseUser

import com.google.firebase.auth.FirebaseAuth


import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnLogOut = findViewById<Button>(R.id.btnLogout)
        mAuth = FirebaseAuth.getInstance()
        btnLogOut.setOnClickListener { view ->
            mAuth!!.signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
    }
}
