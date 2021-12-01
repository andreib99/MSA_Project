package com.fitboys.nutrimax

import HomeFragment
import ProfileFragment
import SettingsFragment
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.text.style.UnderlineSpan

import android.text.SpannableString
import android.view.MenuItem


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
            underlineItem(it);
            when(it.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.profile->setCurrentFragment(profileFragment)
                R.id.settings->setCurrentFragment(settingsFragment)
            }
            true
        }

        bottomNavigationView.selectedItemId=R.id.home
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