package com.fitboys.nutrimax

import HomeFragment
import ProfileFragment
import SettingsFragment
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class FoodActivity : AppCompatActivity()  {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        val b = intent.extras
        mAuth = FirebaseAuth.getInstance()
        var foodName: TextView = findViewById(R.id.etName)
        if (b != null) {
            Log.d(ContentValues.TAG, "Food name = ${b.getString("foodName")}")
            foodName.text = b.getString("foodName")
        }

    }
}