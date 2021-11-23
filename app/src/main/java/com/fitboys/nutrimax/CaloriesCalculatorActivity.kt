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
import androidx.core.content.ContentProviderCompat.requireContext


class CaloriesCalculatorActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onResume(){
        super.onResume();

        val btnCalculate = findViewById<Button>(R.id.btnCalculate)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val etHeight = findViewById<EditText>(R.id.etHeight)
        val etAge = findViewById<EditText>(R.id.etAge)
        val tvActivityLevel=findViewById<AutoCompleteTextView>(R.id.tvActivityLevel)
        val tvGender=findViewById<AutoCompleteTextView>(R.id.tvGender)
        val tvTarget=findViewById<AutoCompleteTextView>(R.id.tvTarget)

        val activityLevels = resources.getStringArray(R.array.activity_levels)
        val activityAdapter = ArrayAdapter(this,R.layout.dropdown_item,activityLevels)
        tvActivityLevel.setAdapter(activityAdapter)


        val genderList=resources.getStringArray(R.array.gender_list)
        val genderAdapter = ArrayAdapter(this,R.layout.dropdown_item,genderList)
        tvGender.setAdapter(genderAdapter)

        val targetList=resources.getStringArray(R.array.target_list)
        val targetAdapter = ArrayAdapter(this,R.layout.dropdown_item,targetList)
        tvTarget.setAdapter(targetAdapter)

        //button press
        btnCalculate.setOnClickListener(View.OnClickListener { view: View? ->
            updateUserProfile(
                etWeight,
                etHeight,
                etAge,
                tvGender,
                tvActivityLevel,
                tvTarget
            )
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories_calculator)

        mAuth = FirebaseAuth.getInstance()

    }

    private fun updateUserProfile(
        etWeight: EditText,
        etHeight: EditText,
        etAge: EditText,
        tvGender: AutoCompleteTextView,
        tvActivity_Level: AutoCompleteTextView,
        tvTarget: AutoCompleteTextView
    ) {
        val weight = Objects.requireNonNull(etWeight.text).toString()
        val height = Objects.requireNonNull(etHeight.text).toString()
        val age = Objects.requireNonNull(etAge.text).toString()
        val gender = Objects.requireNonNull(tvGender.text).toString()
        val activity_level = Objects.requireNonNull(tvActivity_Level.text).toString()
        val target = Objects.requireNonNull(tvTarget.text).toString()

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
                (tvGender as TextView).error = "Error message"
            }
            activity_level == "Activity level..." -> {
                (tvActivity_Level as TextView).error = "Error message"
            }
            target == "Target..." -> {
                (tvTarget as TextView).error = "Error message"
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
                                "caloriesIntake" to result_calories,
                                "remainingCalories" to result_calories,
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

    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user == null) {
            startActivity(Intent(this@CaloriesCalculatorActivity, LoginActivity::class.java))
        }
    }

}
