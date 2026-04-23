package com.employee_management_system.shashank.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.employee_management_system.shashank.activity.ChatActivity
import com.employee_management_system.shashank.databinding.ItemInboxBinding
import com.google.firebase.firestore.QueryDocumentSnapshot

class InboxAdapter(
    val list : ArrayList<QueryDocumentSnapshot>
) : RecyclerView.Adapter<InboxAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemInboxBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val data = list[position]
        holder.binding.leaveId.text = data.id
        holder.binding.senderName.text = data.getString("empName")

        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(
                Intent(holder.itemView.context, ChatActivity::class.java)
                    .putExtra("applicationId", data.id)
                    .putExtra("reportingOfficerId", data.getString("reportingOfficerId"))
                    .putExtra("senderName", data.getString("empName"))
            )
        }
    }

    override fun getItemCount() = list.size

    class ViewHolder(val binding: ItemInboxBinding) : RecyclerView.ViewHolder(binding.root)
}