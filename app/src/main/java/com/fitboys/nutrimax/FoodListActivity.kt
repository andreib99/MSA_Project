package com.fitboys.nutrimax

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import android.os.Bundle
import android.util.Log
import androidx.annotation.Nullable

import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.fitboys.nutrimax.data.model.Food

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

import com.google.firebase.firestore.QuerySnapshot

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener


class FoodListActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    var adapter : FoodAdapter? = null
    var firebaseStore : FirebaseStorage? = null
    var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        recyclerView = findViewById(R.id.recycler)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        val query = FirebaseFirestore.getInstance()
            .collection("foods")

        query.get().addOnCompleteListener(OnCompleteListener<QuerySnapshot?> { task ->
            if (task.exception != null) {
                Log.w(TAG, "get:error" + task.exception!!.message)
            }
        })

        val options: FirestoreRecyclerOptions<Food> = FirestoreRecyclerOptions.Builder<Food>()
            .setQuery(query, Food::class.java)
            .setLifecycleOwner(this)
            .build()

        adapter = FoodAdapter(options)
        recyclerView?.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
    }
}