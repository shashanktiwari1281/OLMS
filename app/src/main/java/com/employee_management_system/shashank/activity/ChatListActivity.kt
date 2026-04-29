package com.employee_management_system.shashank.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.employee_management_system.shashank.R
import com.employee_management_system.shashank.adapter.InboxAdapter
import com.employee_management_system.shashank.databinding.ActivityChatListBinding
import com.employee_management_system.shashank.myApplicationAdapter
import com.employee_management_system.shashank.utils.PreferenceHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class ChatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val firebaseFirestore = Firebase.firestore
        val fromAdmin = intent.getBooleanExtra("fromAdmin",false)
        firebaseFirestore.collection("leaveApplied")
            .whereEqualTo(if (fromAdmin)"reportingOfficerId" else "empId", PreferenceHelper.getUserId(this))
            .orderBy("appliedOn", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnCompleteListener(OnCompleteListener { task: Task<QuerySnapshot?>? ->

                if (task?.getResult()?.isEmpty == true)
                    binding.processingTV.setText(R.string.no_application_found)
                else {
                    val ref = FirebaseDatabase.getInstance(
                        "https://ems-iise-default-rtdb.asia-southeast1.firebasedatabase.app/"
                    ).getReference("Chats")

                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            val chatList = mutableListOf<String>()

                            for (child in snapshot.children) {
                                val key = child.key
                                key?.let { chatList.add(it) }
                            }
                            binding.processingTV.visibility = View.GONE
                            val documentArray = ArrayList<QueryDocumentSnapshot>()
                            val result = task?.result ?: return
                            for (queryDocumentSnapshot in result) {
                                if (chatList.contains(queryDocumentSnapshot.id.replace("@", "")))
                                    documentArray.add(queryDocumentSnapshot)
                            }
                            binding.chatRecyclerView.layoutManager =
                                LinearLayoutManager(this@ChatListActivity)
                            binding.chatRecyclerView.adapter = InboxAdapter(fromAdmin, documentArray)
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })

                }
            })
    }
}