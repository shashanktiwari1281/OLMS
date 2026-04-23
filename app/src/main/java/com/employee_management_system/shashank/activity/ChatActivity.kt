package com.employee_management_system.shashank.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.employee_management_system.shashank.utils.PreferenceHelper
import com.employee_management_system.shashank.adapter.MessageAdapter
import com.employee_management_system.shashank.databinding.ActivityChatBinding
import com.employee_management_system.shashank.logInActivity
import com.employee_management_system.shashank.models.Chat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class ChatActivity : AppCompatActivity() {
    private val pageSize: Int = 50
    private var applicationId: String? = null
    private lateinit var binding: ActivityChatBinding
    private val chatList = mutableListOf<Chat?>()
    private lateinit var chatReference: DatabaseReference
    private var messageAdapter: MessageAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)
            binding.appbar.setPadding(0, systemBars.top, 0, 0)
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            binding.bottomBar.setPadding(0, 0, 0, imeHeight.coerceAtLeast(systemBars.bottom))
            insets
        }

        val reportingOfficerId = intent.getStringExtra("reportingOfficerId")
        applicationId = intent.getStringExtra("applicationId")

        binding.title.text = intent.getStringExtra("senderName")


        val userId = PreferenceHelper.getUserId(this)
        if (userId.isNullOrEmpty()) {
            startActivity(Intent(this@ChatActivity, logInActivity::class.java))
            finish()
            return
        }

        binding.subtitle.text = applicationId
        binding.btnBack.setOnClickListener { finish() }

        binding.etMessage.doOnTextChanged { _,_,_,count ->
            binding.btnSend.isVisible = count > 0
        }

        chatReference = FirebaseDatabase.getInstance(
            "https://ems-iise-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("Chats").child(applicationId?.replace("@", "") ?: "")

        if (reportingOfficerId.isNullOrEmpty() || applicationId.isNullOrEmpty()) {
            Toast.makeText(this, "Something Going Wrong!", Toast.LENGTH_SHORT).show()
            return
        }
        TooltipCompat.setTooltipText(binding.infoBtn, "These messages will disappears after 7 days.")
        loadInitialMessages()

        listenForMessages()

        binding.btnSend.setOnClickListener {

            val msg = binding.etMessage.text.toString().trim()

            if (msg.isNotEmpty()) {
                sendMessage(msg)
                binding.etMessage.setText("")
            }
        }

    }

    private fun loadInitialMessages() {
        val query: Query = chatReference.orderByChild("timestamp").startAt((System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)).toDouble())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (ds in snapshot.getChildren()) {
                    val chat: Chat? = ds.getValue<Chat?>(Chat::class.java)
//                    chatList.add(chat)
                }

                binding.msgRecyclerView.layoutManager =
                    LinearLayoutManager(this@ChatActivity).apply {
                        stackFromEnd = true
                    }

                messageAdapter = MessageAdapter(chatList)
                binding.msgRecyclerView.adapter = messageAdapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun listenForMessages() {

        chatReference
            .orderByChild("timestamp")
            .startAt((System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)).toDouble())
            .addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    val chat = snapshot.getValue(Chat::class.java)

                    chat?.let {
                        messageAdapter?.addNewChat(it)
                        binding.msgRecyclerView.smoothScrollToPosition(messageAdapter?.msgList?.size?:0)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Realtime", error.message)
                }
            })
    }

    private fun sendMessage(message: String) {

        val userId = PreferenceHelper.getUserId(this) ?: return

        val ref = FirebaseDatabase.getInstance(
            "https://ems-iise-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("Chats").child(applicationId?.replace("@", "") ?: "")
        val messageId = ref.push().key ?: return
        val chat = Chat(
            senderId = userId,
            message = message,
            timestamp = System.currentTimeMillis(),
            isSeen = false
        )

        ref.child(messageId).setValue(chat).addOnSuccessListener {
            Log.d("Firebase", "Message Sent Successfully")
//                Toast.makeText(this, "Sent", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.e("Firebase", "Error: ${it.message}")
//                Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}