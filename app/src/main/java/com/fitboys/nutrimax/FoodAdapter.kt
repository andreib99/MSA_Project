package com.fitboys.nutrimax

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fitboys.nutrimax.data.model.Food
import android.view.LayoutInflater
import android.widget.ImageView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException


class FoodAdapter(options: FirestoreRecyclerOptions<Food>, private val listener: OnItemClickListener) : FirestoreRecyclerAdapter<
        Food, FoodAdapter.foodsViewholder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): foodsViewholder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_single_food, parent, false)
        return foodsViewholder(view)
    }

    override fun onBindViewHolder(holder: foodsViewholder, position: Int, model: Food) {
            holder.name.text = model.name
            holder.rating.text = model.rating.toString()
            holder.image_path.text = model.image
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

    inner class foodsViewholder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var name: TextView = itemView.findViewById(R.id.name)
        var rating: TextView = itemView.findViewById(R.id.rating)
        var image: ImageView = itemView.findViewById(R.id.FoodImage)
        var image_path: TextView = itemView.findViewById(R.id.image)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position, this.name.text as String)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, name: String)
    }
}