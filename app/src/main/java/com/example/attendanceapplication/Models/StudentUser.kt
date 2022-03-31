package com.example.attendanceapplication.Models

data class StudentUser(
    val name:String = "",
    val email : String = "",
    val rollno : String = "",
    val uid : String = "",
    val privilege : String = "",
    val department : String = "",
    val courseList : ArrayList<String> = ArrayList()
)
