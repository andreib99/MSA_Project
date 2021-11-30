package com.fitboys.nutrimax

import HomeFragment
import ProfileFragment
import SettingsFragment
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mAuth = FirebaseAuth.getInstance()

        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val homeFragment= HomeFragment()
        val profileFragment=ProfileFragment()
        val settingsFragment=SettingsFragment()

        setCurrentFragment(homeFragment)

        bottomNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.profile->setCurrentFragment(profileFragment)
                R.id.settings->setCurrentFragment(settingsFragment)

            }
            true
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
}