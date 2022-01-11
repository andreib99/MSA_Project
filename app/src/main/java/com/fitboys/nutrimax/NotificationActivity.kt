package com.fitboys.nutrimax

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.fitboys.nutrimax.data.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class NotificationActivity: AppCompatActivity(), NotificationAdapter.OnItemClickListener{

    private var recyclerView: RecyclerView? = null
    var adapter : NotificationAdapter? = null
    var firebaseStore : FirebaseStorage? = null
    var storageReference: StorageReference? = null
    val listener = this
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_list)

        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        val btnBack=findViewById<Button>(R.id.HomeBtnBack)

        val db = Firebase.firestore
        mAuth.currentUser?.uid?.let {
            db.collection("users")
                .document(it).get().addOnSuccessListener { document ->
                    recyclerView = findViewById(R.id.recycler)
                    recyclerView?.layoutManager = LinearLayoutManager(this)
                    val query = FirebaseFirestore.getInstance()
                        .collection("notifications")
                        .whereEqualTo("userId", it)

                    query.get().addOnCompleteListener { task ->
                        if (task.exception != null) {
                            Log.w(ContentValues.TAG, "get:error" + task.exception!!.message)
                        }
                    }

                    val options: FirestoreRecyclerOptions<Notification> = FirestoreRecyclerOptions.Builder<Notification>()
                        .setQuery(query, Notification::class.java)
                        .setLifecycleOwner(this)
                        .build()

                    adapter = NotificationAdapter(options, listener)
                    recyclerView?.adapter = adapter
                }
        }

        btnBack.setOnClickListener{view ->
            startActivity(Intent(this@NotificationActivity, HomeActivity::class.java))
        }


    }

    override fun onItemClick(position: Int, message: String, link: String, id: String) {
        Log.e(ContentValues.TAG, "Id: $id")
        var i = Intent(this@NotificationActivity, HomeActivity::class.java)
        adapter?.notifyItemChanged(position)
        val db = Firebase.firestore
        db.collection("notifications").document(id)
            .delete()
            .addOnSuccessListener { Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }

        if(message == "You exceeded the necessary calories intake!")
        {
            i = Intent(this@NotificationActivity, HomeActivity::class.java)
        }
        else
        {
            if(message == "Someone mentioned you in a comment!")
            {
                i = Intent(this@NotificationActivity, FoodActivity::class.java)
                i.putExtra("foodName", link)
            }
        }

        startActivity(i)
    }
}