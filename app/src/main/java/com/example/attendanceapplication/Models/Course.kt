package com.example.attendanceapplication.Models

data class Course(
    var department : String="",
    var semester : String="",
    var name : String="",
    var uid : String= "",
    var studentsList : ArrayList<String> = ArrayList()
)
