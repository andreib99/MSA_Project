package com.fitboys.nutrimax

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.fitboys.nutrimax.data.model.Comment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CommentAdapter(options: FirestoreRecyclerOptions<Comment>) : FirestoreRecyclerAdapter<
        Comment, CommentAdapter.CommentsViewholder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewholder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_single_comment, parent, false)
        return CommentsViewholder(view)
    }

    override fun onBindViewHolder(holder: CommentsViewholder, position: Int, model: Comment) {

        val db = Firebase.firestore
        var userName = ""
        db.collection("users").document(model.userId)
            .get()
            .addOnSuccessListener { document ->
                Log.d(ContentValues.TAG, "Read document with name ${document.id}")
                userName = document.data?.get("username")?.toString() ?: ""
                Log.d(ContentValues.TAG, "userName: $userName")
                holder.userId.text = userName
            }

        holder.foodId.text = model.foodId
        holder.message.text = model.message
        holder.date.text = model.date

    }

    class CommentsViewholder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var userId: TextView = itemView.findViewById(R.id.userId)
        var foodId: TextView = itemView.findViewById(R.id.foodId)
        var message: TextView = itemView.findViewById(R.id.message)
        var date: TextView = itemView.findViewById(R.id.date)

    }


}