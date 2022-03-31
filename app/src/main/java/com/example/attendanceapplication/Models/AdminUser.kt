package com.example.attendanceapplication.Models

data class AdminUser(
    val name:String = "",
    val email : String = "",
    val uid : String = "",
    val privilege : String = "",
    val department : String = ""
)
