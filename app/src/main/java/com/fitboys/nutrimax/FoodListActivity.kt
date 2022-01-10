package com.fitboys.nutrimax

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
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

import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.QuerySnapshot

import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import android.view.inputmethod.EditorInfo

import android.view.*

import androidx.appcompat.widget.SearchView

import android.content.Intent
import android.widget.Button
import android.widget.Toast


class FoodListActivity : AppCompatActivity(), FoodAdapter.OnItemClickListener{
    private var recyclerView: RecyclerView? = null
    var adapter : FoodAdapter? = null
    var firebaseStore : FirebaseStorage? = null
    var storageReference: StorageReference? = null
    val listener = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        val btnBack=findViewById<Button>(R.id.FoodListBtnBack)

        btnBack.setOnClickListener{view ->
            startActivity(Intent(this@FoodListActivity,HomeActivity::class.java))
        }

        recyclerView = findViewById(R.id.recycler)
        val textView: TextView = findViewById(R.id.textView)
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

        adapter = FoodAdapter(options, listener)
        recyclerView?.adapter = adapter
    }

    override fun onItemClick(position: Int, name: String) {
        Toast.makeText(this, "Selected food: $name", Toast.LENGTH_SHORT).show()
        adapter?.notifyItemChanged(position)
        var i = Intent(this@FoodListActivity, FoodActivity::class.java)
        i.putExtra("foodName", name)
        startActivity(i)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem: MenuItem? = menu.findItem(R.id.actionSearch) as MenuItem?
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText != "") {
                    val query = FirebaseFirestore.getInstance()
                        .collection("foods").orderBy("name")
                        .whereGreaterThanOrEqualTo("name", newText)
                        .whereLessThanOrEqualTo("name", newText +"\uf8ff")
                    val options: FirestoreRecyclerOptions<Food> = FirestoreRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food::class.java)
                        .setLifecycleOwner(this@FoodListActivity)
                        .build()

                    adapter = FoodAdapter(options, listener)
                    recyclerView?.adapter = adapter
                    return true
                }
                else
                {
                    val query = FirebaseFirestore.getInstance()
                        .collection("foods")
                    val options: FirestoreRecyclerOptions<Food> = FirestoreRecyclerOptions.Builder<Food>()
                        .setQuery(query, Food::class.java)
                        .setLifecycleOwner(this@FoodListActivity)
                        .build()

                    adapter = FoodAdapter(options, listener)
                    recyclerView?.adapter = adapter
                    return true
                }


            }
        })
        return true
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