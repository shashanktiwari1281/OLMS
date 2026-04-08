package com.employee_management_system.shashank.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.employee_management_system.shashank.PreferenceHelper
import com.employee_management_system.shashank.databinding.ItemMessageBinding
import com.employee_management_system.shashank.models.Chat

class MessageAdapter(
    val msgList: List<Chat?>
): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = ViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val msg = msgList[position]
        msg?.let {
            with(holder.binding) {
                if (it.senderId == PreferenceHelper.getUserId(holder.itemView.context)) {
                    sentMsg.text = msg.message
                    receivedMsgLayout.visibility = View.GONE
                    sentMsgLayout.visibility = View.VISIBLE
                }
                else{
                    receivedMsg.text = msg.message
                    sentMsgLayout.visibility = View.GONE
                    receivedMsgLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount() = msgList.size

    class ViewHolder(val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root)
}