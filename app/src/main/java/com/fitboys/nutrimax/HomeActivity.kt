package com.fitboys.nutrimax

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val calories = findViewById<TextView>(R.id.calories)
        val recordFood = findViewById<Button>(R.id.recordFood)
        val addFood = findViewById<Button>(R.id.addFood)
        val calculator=findViewById<Button>(R.id.buttonCalculator)
        mAuth = FirebaseAuth.getInstance()

        val db = Firebase.firestore
        mAuth?.currentUser?.uid?.let {
            db.collection("users")
                .document(it).get()
                .addOnSuccessListener { document ->
                        Log.d(TAG, "Read document with ID ${document.id}")
                        calories.text = document.data?.get("remainingCalories").toString()
                    }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents $exception")
                }

            recordFood.setOnClickListener { view ->
                startActivity(Intent(this@HomeActivity, FoodListActivity::class.java))
            }

            addFood.setOnClickListener { view ->
                startActivity(Intent(this@HomeActivity, AddFoodActivity::class.java))
            }
            calculator.setOnClickListener{view ->
                startActivity(Intent(this@HomeActivity,CaloriesCalculatorActivity::class.java))
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
}