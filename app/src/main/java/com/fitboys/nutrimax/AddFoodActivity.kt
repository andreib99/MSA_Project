package com.fitboys.nutrimax

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fitboys.nutrimax.data.model.Food
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask


class AddFoodActivity : AppCompatActivity()  {

    private var mAuth: FirebaseAuth? = null
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var fileName: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        val etName = findViewById<EditText>(R.id.etName)
        val etQuantity = findViewById<EditText>(R.id.etQuantity)
        val etCalories = findViewById<EditText>(R.id.etCalories)
        val etFats = findViewById<EditText>(R.id.etFats)
        val etProteins = findViewById<EditText>(R.id.etProteins)
        val etCarbs = findViewById<EditText>(R.id.etCarbs)
        val btnAddFood = findViewById<Button>(R.id.btnAddFood)
        val btnSelect = findViewById<Button>(R.id.btnImage);
        mAuth = FirebaseAuth.getInstance()

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        btnSelect.setOnClickListener(View.OnClickListener {view: View? -> selectImageFromGallery()
        })


        btnAddFood.setOnClickListener(View.OnClickListener { view: View? ->
            createFood(
                etName,
                etQuantity,
                etCalories,
                etFats,
                etProteins,
                etCarbs,
            )
        })
    }

    private fun selectImageFromGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Please select..."
            ),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == PICK_IMAGE_REQUEST
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null
        ) {

            // Get the Uri of data
            filePath = data.data
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        fileName = UUID.randomUUID().toString() +".jpg"

        val database = FirebaseDatabase.getInstance()
        val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")

        refStorage.putFile(fileUri)
            .addOnSuccessListener(
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                    }
                })

            ?.addOnFailureListener(OnFailureListener { e ->
                print(e.message)
            })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createFood(
        etName: EditText,
        etQuantity: EditText,
        etCalories: EditText,
        etFats: EditText,
        etProteins: EditText,
        etCarbs: EditText
    ){
        val name = Objects.requireNonNull(etName.text).toString()
        val quantity = Objects.requireNonNull(etQuantity.text).toString()
        val calories = Objects.requireNonNull(etCalories.text).toString()
        val fats = Objects.requireNonNull(etFats.text).toString()
        val proteins = Objects.requireNonNull(etProteins.text).toString()
        val carbs = Objects.requireNonNull(etCarbs.text).toString()

        when {
            TextUtils.isEmpty(name) -> {
                etName.error = "Weight cannot be empty"
                etName.requestFocus()
            }
            TextUtils.isEmpty(quantity) -> {
                etQuantity.error = "Weight cannot be empty"
                etQuantity.requestFocus()
            }
            TextUtils.isEmpty(calories) -> {
                etCalories.error = "Height cannot be empty"
                etCalories.requestFocus()
            }
            TextUtils.isEmpty(fats) -> {
                etFats.error = "Age cannot be empty"
                etFats.requestFocus()
            }
            TextUtils.isEmpty(proteins) -> {
                etProteins.error = "Age cannot be empty"
                etProteins.requestFocus()
            }
            TextUtils.isEmpty(carbs) -> {
                etCarbs.error = "Age cannot be empty"
                etCarbs.requestFocus()
            }
            else -> {
                val db = Firebase.firestore

                val currentDateTime = LocalDateTime.now()


                filePath?.let { uploadImageToFirebase(it) }


                val food = Food(
                    name = name,
                    quantity = quantity.toInt(),
                    calories = calories.toInt(),
                    fats = fats.toInt(),
                    proteins = proteins.toInt(),
                    carbohydrates = carbs.toInt(),
                    recordedDate = currentDateTime.format(DateTimeFormatter.ISO_DATE),
                    rating = 0.0,
                    image = fileName.orEmpty()
                )

                db.collection("foods")
                    .add(food)
                    .addOnSuccessListener {
                        Log.d(ContentValues.TAG, "DocumentSnapshot added")
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error adding document", e)
                    }


                Toast.makeText(
                    this@AddFoodActivity,
                    "Food created successfully!",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this@AddFoodActivity, HomeActivity::class.java))

            }
        }

    }
}