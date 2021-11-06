package com.fitboys.nutrimax

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color

import com.google.firebase.auth.FirebaseAuth


import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import android.widget.ArrayAdapter
import android.widget.TextView


class CaloriesCalculatorActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories_calculator)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val etHeight = findViewById<EditText>(R.id.etHeight)
        val etAge = findViewById<EditText>(R.id.etAge)
        val etGender = findViewById<Spinner>(R.id.etGender)
        val etActivity_Level = findViewById<Spinner>(R.id.etActivity_Level)
        val etTarget = findViewById<Spinner>(R.id.etTarget)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)
        mAuth = FirebaseAuth.getInstance()

        //Gender possible options
        val GenderOptions: MutableList<String?> = ArrayList()
        GenderOptions.add(0, "Gender...")
        GenderOptions.add("Male")
        GenderOptions.add("Female")

        val arrayAdapter: ArrayAdapter<String?> =
            ArrayAdapter<String?>(this, android.R.layout.simple_list_item_1, GenderOptions)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        etGender.adapter = arrayAdapter

        //activity level possible options
        val ActivityOptions: MutableList<String?> = ArrayList()
        ActivityOptions.add(0, "Activity level...")
        ActivityOptions.add("Sedentary")
        ActivityOptions.add("Lightly active")
        ActivityOptions.add("Active")
        ActivityOptions.add("Very active")

        val arrayAdapter1: ArrayAdapter<String?> =
            ArrayAdapter<String?>(this, android.R.layout.simple_list_item_1, ActivityOptions)
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        etActivity_Level.adapter = arrayAdapter1


        //Target possible options
        val TargetOptions: MutableList<String?> = ArrayList()
        TargetOptions.add(0, "Target...")
        TargetOptions.add("Lose weight")
        TargetOptions.add("Maintain")
        TargetOptions.add("Gain weight")

        val arrayAdapter2: ArrayAdapter<String?> =
            ArrayAdapter<String?>(this, android.R.layout.simple_list_item_1, TargetOptions)
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        etTarget.adapter = arrayAdapter2


        //button press
        btnCalculate.setOnClickListener(View.OnClickListener { view: View? ->
            updateUserProfile(
                etWeight,
                etHeight,
                etAge,
                etGender,
                etActivity_Level,
                etTarget
            )
        })
    }

    private fun updateUserProfile(
        etWeight: EditText,
        etHeight: EditText,
        etAge: EditText,
        etGender: Spinner,
        etActivity_Level: Spinner,
        etTarget: Spinner
    ) {
        val weight = Objects.requireNonNull(etWeight.text).toString()
        val height = Objects.requireNonNull(etHeight.text).toString()
        val age = Objects.requireNonNull(etAge.text).toString()
        val gender = Objects.requireNonNull(etGender.selectedItem).toString()
        val activity_level = Objects.requireNonNull(etActivity_Level.selectedItem).toString()
        val target = Objects.requireNonNull(etTarget.selectedItem).toString()

        when {
            TextUtils.isEmpty(weight) -> {
                etWeight.error = "Weight cannot be empty"
                etWeight.requestFocus()
            }
            TextUtils.isEmpty(height) -> {
                etHeight.error = "Height cannot be empty"
                etHeight.requestFocus()
            }
            TextUtils.isEmpty(age) -> {
                etAge.error = "Age cannot be empty"
                etAge.requestFocus()
            }
            gender == "Gender..." -> {
                (etGender.selectedView as TextView).error = "Error message"
            }
            activity_level == "Activity level..." -> {
                (etActivity_Level.selectedView as TextView).error = "Error message"
            }
            target == "Target..." -> {
                (etTarget.selectedView as TextView).error = "Error message"
            }
            else -> {
                val result_calories = calculateCalories(weight, height, age, gender, activity_level, target)
                val db = Firebase.firestore
                mAuth?.currentUser?.uid?.let {
                    db.collection("users")
                        .document(it).update(
                            mapOf(
                                "weight" to weight,
                                "height" to height,
                                "age" to age,
                                "gender" to gender,
                                "activityLevel" to activity_level,
                                "caloriesIntake" to result_calories
                            )
                        )
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "DocumentSnapshot updated")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error updating document", e)
                        }
                }
                startActivity(Intent(this@CaloriesCalculatorActivity, HomeActivity::class.java))
            }
        }
    }

    private fun calculateCalories(
        weight: String,
        height: String,
        age: String,
        gender: String,
        activity_level: String,
        target: String
    ): Int {
        var calories = 0

        if (gender == "Male") {
            calories =
                (655.0955f + (9.5634f * weight.toInt()) + (1.8496f * height.toInt()) - (4.6756f * age.toInt())).toInt()
        } else {
            calories =
                (66.4730f + (13.7516 * weight.toInt()) + (5.0033f * height.toInt()) - (6.7550 * age.toInt())).toInt()
        }

        if (activity_level == "Sedentary")
        {
            calories = (calories * 1.25f).toInt()
        }

        if (activity_level == "Lightly active")
        {
            calories = (calories * 1.5f).toInt()
        }

        if (activity_level == "Active")
        {
            calories = (calories * 1.725f).toInt()
        }

        if (activity_level == "Very active")
        {
            calories = (calories * 1.9).toInt()
        }

        if (target == "Lose weight")
        {
            calories -= 500
        }

        if (target == "Gain weight")
        {
            calories += 500
        }

        return calories
    }

}
