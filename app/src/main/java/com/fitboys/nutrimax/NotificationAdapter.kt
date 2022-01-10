package com.fitboys.nutrimax

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.fitboys.nutrimax.data.model.Notification


class NotificationAdapter(options: FirestoreRecyclerOptions<Notification>, private val listener: OnItemClickListener) : FirestoreRecyclerAdapter<
        Notification, NotificationAdapter.notificationsViewholder>(options) {

    val data = options

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): notificationsViewholder {
        val view: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_single_notification, parent, false)
        return notificationsViewholder(view)
    }

    override fun onBindViewHolder(holder: notificationsViewholder, position: Int, model: Notification) {
            holder.userId.text = model.userId
            holder.message.text = model.message
            holder.timestamp.text = model.timestamp
            holder.link.text = model.link

    }

    inner class notificationsViewholder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var userId: TextView = itemView.findViewById(R.id.userId)
        var message: TextView = itemView.findViewById(R.id.message)
        var timestamp: TextView = itemView.findViewById(R.id.timestamp)
        var link: TextView = itemView.findViewById(R.id.link)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position, this.message.text as String, this.link.text as String, snapshots.getSnapshot(position).id)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, message: String, link: String, id: String)
    }
}