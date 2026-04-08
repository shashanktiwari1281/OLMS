package com.employee_management_system.shashank.models

data class Chat(
    var senderId: String? = null,
    var message: String? = null,
    var timestamp: Long = 0,
    var isSeen: Boolean = false
)
