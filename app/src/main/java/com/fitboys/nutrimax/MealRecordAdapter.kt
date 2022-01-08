package com.fitboys.nutrimax

import android.content.ContentValues
import android.graphics.BitmapFactory

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.util.HashMap


class MealRecordAdapter(private val data: MutableList<HashMap<String, String>>) : RecyclerView.Adapter<MealRecordAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_single_meal_record_food, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mAuth = FirebaseAuth.getInstance()
        val food = data[position]
        Log.d(ContentValues.TAG, "$food")
        Log.d(ContentValues.TAG, "${food["id"]}")
        holder.id.text = food["id"]


        val db = Firebase.firestore

        db.collection("foods")
            .document(holder.id.text as String).get().addOnSuccessListener() { document ->
                val currentUser = mAuth.currentUser?.uid
                if (currentUser != "") {
                    holder.name.text = document.data?.get("name").toString()
                    holder.image_path.text = document.data?.get("image").toString()
                    Log.d(ContentValues.TAG, "${holder.image_path.text}")

                    val mStoreReference = FirebaseStorage.getInstance().reference
                        .child("images/${holder.image_path.text}")
                    Log.d(ContentValues.TAG, "image = ${holder.image_path.text}")
                    try {
                        val localFile = File.createTempFile("food", "jpg")
                        mStoreReference.getFile(localFile)
                            .addOnSuccessListener {
                                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                                (holder.image).setImageBitmap(bitmap)
                            }.addOnFailureListener { }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }



    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var id: TextView = itemView.findViewById(R.id.id)
        var name: TextView = itemView.findViewById(R.id.name)
        var image: ImageView = itemView.findViewById(R.id.FoodImage)
        var image_path: TextView = itemView.findViewById(R.id.image)

    }
}


