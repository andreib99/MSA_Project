package com.fitboys.nutrimax

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import android.widget.*
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*

class MealRecordActivity : AppCompatActivity(), MealRecordAdapter.OnItemClickListener{

    private var mAuth: FirebaseAuth? = null
    private lateinit var date : String
    private var recyclerView: RecyclerView? = null
    var adapter : MealRecordAdapter? = null
    private lateinit var tvDate : TextView
    // date picker
    var day = 0
    var month: Int = 0
    var year: Int = 0
    var cal = Calendar.getInstance()
    val listener = this


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.meal_record_activity)

        mAuth = FirebaseAuth.getInstance()

        tvDate=findViewById(R.id.tvDate)
        val remainingCalories=findViewById<TextView>(R.id.textView7)
        val proteins=findViewById<TextView>(R.id.proteins)
        val fats=findViewById<TextView>(R.id.fats)
        val carbohydrates=findViewById<TextView>(R.id.carbo)
        val progressText=findViewById<TextView>(R.id.text_view_progress)
        val btnBack=findViewById<Button>(R.id.MealRecordBackBtn)
        val progressBar=findViewById<ProgressBar>(R.id.progressBar)

        var history: HashMap<String, MutableList<HashMap<String, String>>>
        var totalCalories = 0
        var totalCarbohydrates = 0
        var totalProteins = 0
        var totalFats = 0

        setDate(tvDate)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        tvDate.setOnClickListener{view ->
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            DatePickerDialog(this@MealRecordActivity,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnBack.setOnClickListener{view ->
            startActivity(Intent(this@MealRecordActivity,HomeActivity::class.java))
        }

        val db = Firebase.firestore

        mAuth!!.currentUser?.uid?.let {
            db.collection("users")
                .document(it)
                .get()
                .addOnSuccessListener { document ->
                    try {
                        history = document.data?.get("history") as HashMap<String, MutableList<HashMap<String, String>>>
                        Log.d(ContentValues.TAG, "history = ${history}.")

                        var percentage = 0
                        progressText.text = "0%"
                        progressBar.progress = percentage

                        for (i in history[date]!!)
                        {
                            //Log.d(ContentValues.TAG, "Pair: $food_key = $food_value")
                            totalCalories += i["calories"]?.toInt() ?: 0
                            totalCarbohydrates += i["carbohydrates"]?.toInt() ?: 0
                            totalProteins += i["proteins"]?.toInt() ?: 0
                            totalFats += i["fats"]?.toInt() ?: 0
                        }

                        val caloriesIntake = document.data?.get("caloriesIntake").toString().toInt()

                        remainingCalories.text = totalCalories.toString()
                        Log.d(ContentValues.TAG, "totalCalories: $totalCalories")
                        Log.d(ContentValues.TAG, "totalCarbohydrates: $totalCarbohydrates")
                        Log.d(ContentValues.TAG, "totalProteins: $totalProteins")
                        Log.d(ContentValues.TAG, "totalFats: $totalFats")

                        proteins.text = totalProteins.toString() + "g"
                        fats.text = totalFats.toString() + "g"
                        carbohydrates.text = totalCarbohydrates.toString() + "g"

                        percentage = totalCalories * 100 / caloriesIntake
                        progressText.text = "$percentage%"
                        progressBar.progress = percentage

                        recyclerView = findViewById(R.id.recycler)
                        recyclerView?.layoutManager = LinearLayoutManager(this)
                        adapter = history[date]?.let { it1 -> MealRecordAdapter(it1, listener) }
                        recyclerView?.adapter = adapter
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    override fun onItemClick(position: Int, name: String) {
        Toast.makeText(this, "Selected food: $name", Toast.LENGTH_SHORT).show()
        adapter?.notifyItemChanged(position)
        var i = Intent(this@MealRecordActivity, FoodActivity::class.java)
        i.putExtra("foodName", name)
        startActivity(i)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun setDate(view: TextView) {

        val b = intent.extras
        if (b != null) {
            date = b.getString("date").toString()
            val temp = date.split('.')

            cal.set(Calendar.YEAR, temp[0].toInt())
            cal.set(Calendar.MONTH, temp[1].toInt() - 1)
            cal.set(Calendar.DAY_OF_MONTH, temp[2].toInt())

            // set date for users
            val myFormat = "EEE, MMM dd"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            tvDate.text = sdf.format(cal.time)

            // set date for db
            val currentDateTime = SimpleDateFormat("yyyy.MM.dd")
            date = currentDateTime.format(cal.time)
            Log.d(ContentValues.TAG, date)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateDateInView() {
        val myFormat = "EEE, MMM dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        tvDate.text = sdf.format(cal.time)

        val currentDateTime = SimpleDateFormat("yyyy.MM.dd")
        var i = Intent(
            this@MealRecordActivity,
            MealRecordActivity::class.java
        )
        i.putExtra("date", currentDateTime.format(cal.time))
        startActivity(i)
    }

}