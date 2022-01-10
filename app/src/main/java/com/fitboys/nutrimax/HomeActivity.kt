package com.fitboys.nutrimax

import HomeFragment
import ProfileFragment
import SettingsFragment
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.firestore.DocumentChange


class HomeActivity : AppCompatActivity() {
    private var mAuth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val settingsFragment = SettingsFragment()


        setCurrentFragment(homeFragment)


        bottomNavigationView.setOnItemSelectedListener {
            underlineItem(it);
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.profile -> setCurrentFragment(profileFragment)
                R.id.settings -> setCurrentFragment(settingsFragment)
            }
            true
        }

        bottomNavigationView.selectedItemId = R.id.home

        mAuth.currentUser?.uid?.let {
            db.collection("users")
                .document(it).get().addOnSuccessListener { document ->
                    if (document != null) {
                        db.collection("notifications")
                            .whereEqualTo("userId", it)
                            .whereEqualTo("read", false)
                            .addSnapshotListener { value, e ->
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e)
                                    return@addSnapshotListener
                                }
                                if (value != null) {
                                    Log.e(TAG, "Value count: ${value.count()}")
                                }
                                val notifications = ArrayList<String>()
                                if (value != null) {
                                    for (doc in value.documentChanges) {
                                        if (doc.type == DocumentChange.Type.ADDED)
                                        {
                                            doc.document.getString("message")?.let {
                                                notifications.add(it)
                                            }
                                            db.collection("notifications")
                                                .document(doc.document.id).update("read", true)
                                        }
                                    }
                                }
                                if (notifications.count() == 1) {
                                    Log.e(TAG, "Notifications: $notifications")
                                    Toast.makeText(
                                        this@HomeActivity,
                                        notifications[0],
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    if (notifications.count() > 1) {
                                        Toast.makeText(
                                            this@HomeActivity,
                                            "You have ${notifications.count()} new notifications!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                    }
                }

        }
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user == null) {
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    private fun underlineItem(item: MenuItem){
        val constraintLayout : ConstraintLayout = findViewById(R.id.home_constraint_layout)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.setHorizontalBias(
            R.id.underline,
            getItemPosition(item.itemId)
        )
        constraintSet.applyTo(constraintLayout)

    }

    private fun getItemPosition(itemId: Int): Float {
        return when (itemId) {
            R.id.profile -> 0.0f
            R.id.home -> 0.5f
            R.id.settings -> 1.0f
            else -> 0.5f
        }
    }
}