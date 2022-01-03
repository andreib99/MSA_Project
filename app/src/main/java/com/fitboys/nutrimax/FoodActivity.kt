package com.fitboys.nutrimax

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.fitboys.nutrimax.data.model.Comment
import com.fitboys.nutrimax.data.model.Food
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class FoodActivity : AppCompatActivity()  {
    private var mAuth: FirebaseAuth? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)
        val b = intent.extras

        mAuth = FirebaseAuth.getInstance()

        var foodName: TextView = findViewById(R.id.etName)
        var foodImage: ImageView = findViewById(R.id.FoodImage)
        var foodquantity: TextView = findViewById(R.id.quantity)
        var foodcalories: TextView = findViewById(R.id.calories)
        var foodcarbohydrates: TextView = findViewById(R.id.carbohydrates)
        var foodproteins: TextView = findViewById(R.id.proteins)
        var foodfats: TextView = findViewById(R.id.fats)
        var star1 : ImageView = findViewById(R.id.Star1)
        var star2 : ImageView = findViewById(R.id.Star2)
        var star3 : ImageView = findViewById(R.id.Star3)
        var star4 : ImageView = findViewById(R.id.Star4)
        var star5 : ImageView = findViewById(R.id.Star5)
        val newComment = findViewById<EditText>(R.id.newComment)
        val btnAddComment = findViewById<Button>(R.id.btnAddComment)

        if (b != null) {
            Log.d(TAG, "Food name = ${b.getString("foodName")}")
            foodName.text = b.getString("foodName")
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler)
        recyclerView?.layoutManager = LinearLayoutManager(this)


        var calories: String
        var carbohydrates: String
        var fats: String
        var proteins: String
        var quantity: String
        var rating: String
        var image: String
        var foodId = ""
        var ratings : HashMap<String, Int> = HashMap()

        val db = Firebase.firestore

        btnAddComment.setOnClickListener(View.OnClickListener { view: View? ->
            mAuth!!.currentUser?.uid?.let { addComment(foodId, it, newComment, foodName) }
        })


        db.collection("foods")
            .whereEqualTo("name", foodName.text)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    calories = document.data["calories"].toString()
                    carbohydrates = document.data["carbohydrates"].toString()
                    fats = document.data["fats"].toString()
                    proteins = document.data["proteins"].toString()
                    quantity = document.data["quantity"].toString()
                    rating = document.data["rating"].toString()
                    image = document.data["image"].toString()

                    foodquantity.text = "Quantity: $quantity"
                    foodcalories.text = "Calories: $calories"
                    foodcarbohydrates.text = "Carbohydrates: $carbohydrates"
                    foodproteins.text = "Proteins: $proteins"
                    foodfats.text = "Fats: $fats"
                    foodId = document.id
                    try {
                        ratings = document.data["ratingsList"] as HashMap<String, Int>
                    }catch (e : Exception)
                    {
                        e.printStackTrace()
                    }

                    Log.d(TAG, "ratings = ${ratings}.")

                    //Reading Image from db

                    val mStoreReference = FirebaseStorage.getInstance().reference
                        .child("images/${image}")
                    Log.d(TAG, "image = $image")
                    try {
                        val localFile = File.createTempFile("food", "jpg")
                        mStoreReference.getFile(localFile)
                            .addOnSuccessListener {
                                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                                (foodImage).setImageBitmap(bitmap)
                            }.addOnFailureListener { }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    initStars(rating.toDouble(), star1, star2, star3, star4, star5)

                    val query = FirebaseFirestore.getInstance()
                        .collection("comments").whereEqualTo("foodId", foodId).orderBy("date")

                    query.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot?> { task ->
                        if (task.exception != null) {
                            Log.w(TAG, "get:error" + task.exception!!.message)
                        }
                    })

                    val options: FirestoreRecyclerOptions<Comment> = FirestoreRecyclerOptions.Builder<Comment>()
                        .setQuery(query, Comment::class.java)
                        .setLifecycleOwner(this)
                        .build()

                    val adapter = CommentAdapter(options)

                    recyclerView?.adapter = adapter


                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        star1.setOnClickListener {
            // your code to perform when the user clicks on the ImageView
            Toast.makeText(this@FoodActivity, "You gave 1 star to this food.", Toast.LENGTH_SHORT).show()

            db.collection("foods")
                .document(foodId).get().addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        val currentUser = mAuth!!.currentUser?.uid
                        if (currentUser != "")
                        {
                            ratings[currentUser.toString()] = 1
                            db.collection("foods")
                                .document(foodId).update("ratingsList", ratings)

                            var sum = 0
                            for ((_, v) in ratings) {
                                sum += v
                            }
                            val finalRating = sum / ratings.count().toDouble()
                            db.collection("foods")
                                .document(foodId).update("rating", finalRating)

                            initStars(finalRating, star1, star2, star3, star4, star5)
                        }

                    }
                }

        }

        star2.setOnClickListener {
            // your code to perform when the user clicks on the ImageView
            Toast.makeText(this@FoodActivity, "You gave 2 stars to this food.", Toast.LENGTH_SHORT).show()

            db.collection("foods")
                .document(foodId).get().addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        val currentUser = mAuth!!.currentUser?.uid
                        if (currentUser != "")
                        {
                            ratings[currentUser.toString()] = 2
                            db.collection("foods")
                                .document(foodId).update("ratingsList", ratings)

                            var sum = 0
                            for ((_, v) in ratings) {
                                sum += v
                            }
                            val finalRating = sum / ratings.count().toDouble()
                            db.collection("foods")
                                .document(foodId).update("rating", finalRating)

                            initStars(finalRating, star1, star2, star3, star4, star5)
                        }
                    }
                }
        }

        star3.setOnClickListener {
            // your code to perform when the user clicks on the ImageView
            Toast.makeText(this@FoodActivity, "You gave 3 stars to this food.", Toast.LENGTH_SHORT).show()

            db.collection("foods")
                .document(foodId).get().addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        val currentUser = mAuth!!.currentUser?.uid
                        if (currentUser != "")
                        {
                            ratings[currentUser.toString()] = 3
                            db.collection("foods")
                                .document(foodId).update("ratingsList", ratings)
                            var sum = 0
                            for ((_, v) in ratings) {
                                sum += v
                            }
                            val finalRating = sum / ratings.count().toDouble()
                            db.collection("foods")
                                .document(foodId).update("rating", finalRating)

                            initStars(finalRating, star1, star2, star3, star4, star5)
                        }
                    }
                }

        }

        star4.setOnClickListener {
            // your code to perform when the user clicks on the ImageView
            Toast.makeText(this@FoodActivity, "You gave 4 stars to this food.", Toast.LENGTH_SHORT).show()

            db.collection("foods")
                .document(foodId).get().addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        val currentUser = mAuth!!.currentUser?.uid
                        if (currentUser != "")
                        {
                            ratings[currentUser.toString()] = 4
                            db.collection("foods")
                                .document(foodId).update("ratingsList", ratings)

                            var sum = 0
                            for ((_, v) in ratings) {
                                sum += v
                            }
                            val finalRating = sum / ratings.count().toDouble()
                                db.collection("foods")
                                .document(foodId).update("rating", finalRating)

                            initStars(finalRating, star1, star2, star3, star4, star5)
                        }
                    }
                }
        }

        star5.setOnClickListener {
            // your code to perform when the user clicks on the ImageView
            Toast.makeText(this@FoodActivity, "You gave 5 stars to this food.", Toast.LENGTH_SHORT).show()

            db.collection("foods")
                .document(foodId).get().addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        val currentUser = mAuth!!.currentUser?.uid
                        if (currentUser != "")
                        {
                            ratings[currentUser.toString()] = 5
                            db.collection("foods")
                                .document(foodId).update("ratingsList", ratings)

                            var sum = 0
                            for ((_, v) in ratings) {
                                sum += v
                            }
                            val finalRating = sum / ratings.count().toDouble()
                            db.collection("foods")
                                .document(foodId).update("rating", finalRating)

                            initStars(finalRating, star1, star2, star3, star4, star5)
                        }
                    }
                }
        }
    }

    fun setStarsToEmpty(star1 : ImageView, star2 : ImageView, star3 : ImageView, star4 : ImageView, star5 : ImageView){
        star1.setImageResource(R.drawable.star_unfilled)
        star2.setImageResource(R.drawable.star_unfilled)
        star3.setImageResource(R.drawable.star_unfilled)
        star4.setImageResource(R.drawable.star_unfilled)
        star5.setImageResource(R.drawable.star_unfilled)
    }

    fun initStars(rating : Double, star1 : ImageView, star2 : ImageView, star3 : ImageView, star4 : ImageView, star5 : ImageView)
    {
        setStarsToEmpty(star1, star2, star3, star4, star5)
        if (rating >= 1)
        {
            star1.setImageResource(R.drawable.star_filled)
        }
        if (rating > 1 && rating < 2)
        {
            star2.setImageResource(R.drawable.star_half_filled)
        }
        if (rating >= 2)
        {
            star2.setImageResource(R.drawable.star_filled)
        }
        if (rating > 2 && rating < 3)
        {
            star3.setImageResource(R.drawable.star_half_filled)
        }
        if (rating >= 3)
        {
            star3.setImageResource(R.drawable.star_filled)
        }
        if (rating > 3 && rating < 4)
        {
            star4.setImageResource(R.drawable.star_half_filled)
        }
        if (rating >= 4)
        {
            star4.setImageResource(R.drawable.star_filled)
        }
        if (rating > 4 && rating < 5)
        {
            star5.setImageResource(R.drawable.star_half_filled)
        }
        if (rating == 5.0)
        {
            star5.setImageResource(R.drawable.star_filled)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun addComment(foodId: String, UserId: String, newComment: EditText, foodName: TextView)
    {
        val message = Objects.requireNonNull(newComment.text).toString()

        if (TextUtils.isEmpty(message))
        {
            newComment.error = "Comment cannot be empty"
            newComment.requestFocus()
        }
        else
        {
            val currentDateTime = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss")
            val db = Firebase.firestore
            mAuth?.currentUser?.uid?.let {
            db.collection("comments")
                    .add(
                        Comment(
                            UserId,
                            foodId,
                            message,
                            currentDateTime.format(Date()),
                        )
                    )
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot added")
                        Toast.makeText(
                            this@FoodActivity,
                            "Comment added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        newComment.text.clear()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
        }

    }


}