package com.employee_management_system.shashank.adapter

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.employee_management_system.shashank.utils.PreferenceHelper
import com.employee_management_system.shashank.databinding.ItemMessageBinding
import com.employee_management_system.shashank.models.Chat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

class MessageAdapter(
    val msgList: MutableList<Chat?>
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
                    sentMsgTime.text = formatTimestamp(msg.timestamp)
                    receivedMsgLayout.visibility = View.GONE
                    sentMsgLayout.visibility = View.VISIBLE
                }
                else{
                    receivedMsg.text = msg.message
                    receivedMsgTime.text = formatTimestamp(msg.timestamp)
                    sentMsgLayout.visibility = View.GONE
                    receivedMsgLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount() = msgList.size

    fun addNewChat(chat: Chat){
        msgList.add(chat)
        notifyItemInserted(msgList.size - 1)
    }

    fun formatTimestamp(timestamp: Long): String {
        val cal = Calendar.getInstance()
        val todayCal = Calendar.getInstance()
        val yesterdayCal = Calendar.getInstance()

        cal.timeInMillis = timestamp
        yesterdayCal.add(Calendar.DAY_OF_YEAR, -1)

        // ⏰ Time format (10:07 PM)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val time = timeFormat.format(cal.time)

        return when {
            isSameDay(cal, todayCal) -> "Today, $time"

            isSameDay(cal, yesterdayCal) -> "Yesterday, $time"

            else -> {
                val diff = ((todayCal.timeInMillis - cal.timeInMillis) / (1000 * 60 * 60 * 24))

                val dateFormat = if (diff <= 7)
                    SimpleDateFormat("d MMMM", Locale.getDefault())
                else
                    SimpleDateFormat("d MMM yyyy", Locale.getDefault())

                "${dateFormat.format(cal.time)}, $time"
            }
        }
    }

    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    class ViewHolder(val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root)
}