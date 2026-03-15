package com.employee_management_system.shashank.models

data class Chat(
    val senderId: String,
    val message: String,
    val timestamp: Long,
    val isSeen: Boolean
)
