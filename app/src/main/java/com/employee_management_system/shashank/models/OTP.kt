package com.employee_management_system.shashank.models

data class OTP(
    val mobile: String,
    val otp: String,
    val isPending: Boolean
)
