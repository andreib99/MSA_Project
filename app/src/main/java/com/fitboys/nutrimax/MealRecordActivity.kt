package com.fitboys.nutrimax

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MealRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meal_record_activity)

        val tvDate=findViewById<TextView>(R.id.tvDate)
        val btnBack=findViewById<Button>(R.id.mealRecordBackBtn)

        setDate(tvDate)
        btnBack.setOnClickListener{view ->
            startActivity(Intent(this@MealRecordActivity,HomeActivity::class.java))
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun setDate(view: TextView) {
        val today = Calendar.getInstance().time //getting date
        val formatter = SimpleDateFormat("EEE, MMM dd")
        val date: String = formatter.format(today)
        view.text = date
    }
}