package com.example.attendanceapplication.Models

data class JsonObject(
    var department : String="",
    var semester : String="",
    var name : String="",
    var statslist : ArrayList<StudentsPandAStats> = ArrayList()
)
