package com.example.attendanceapplication.Models

data class CourseNamePresentAbsent(
    var name : String = "",
    var uid : String = "",
    var totPresentee : Int = 0,
    var totAbsentee : Int = 0
)

