package com.employee_management_system.shashank.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.employee_management_system.shashank.PreferenceHelper
import com.employee_management_system.shashank.R
import com.employee_management_system.shashank.databinding.ActivityChatBinding
import com.employee_management_system.shashank.logInActivity
import com.employee_management_system.shashank.models.Chat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class ChatActivity : AppCompatActivity() {
    private val pageSize: Int = 20
    private var lastVisibleTimestamp = Long.Companion.MAX_VALUE
    private val isLoading = false
    private var applicationId: String? = null
    private lateinit var binding: ActivityChatBinding
    private val chatList = mutableListOf<Chat?>()
    private lateinit var chatReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val reportingOfficerId = intent.getStringExtra("reportingOfficerId")
        applicationId = intent.getStringExtra("applicationId")

        val userId = PreferenceHelper.getUserId(this)
        if (userId.isNullOrEmpty()) {
            startActivity(Intent(this@ChatActivity, logInActivity::class.java))
            finish()
            return
        }

        chatReference = FirebaseDatabase.getInstance()
            .getReference("Chats")
            .child(applicationId?:"")

        if (reportingOfficerId.isNullOrEmpty() || applicationId.isNullOrEmpty()){
            Toast.makeText(this, "Something Going Wrong!", Toast.LENGTH_SHORT).show()
            return
        }

        loadInitialMessages()

    }
    private fun loadInitialMessages() {
        val query: Query = chatReference
            .orderByChild("timestamp")
            .limitToLast(pageSize)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()

                for (ds in snapshot.getChildren()) {
                    val chat: Chat? = ds.getValue<Chat?>(Chat::class.java)
                    chatList.add(chat)

                    lastVisibleTimestamp = chat?.timestamp?:0
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}