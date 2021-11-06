package com.fitboys.nutrimax

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val btnLogOut = findViewById<Button>(R.id.btnLogout)
        mAuth = FirebaseAuth.getInstance()
        btnLogOut.setOnClickListener { view ->
            mAuth!!.signOut()
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user == null) {
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        }
    }
}