package com.example.attendanceapplication.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AttendanceForTheDay(
    var date : String = "",
    var presentee : ArrayList<String> = ArrayList(),
    var absentee : ArrayList<String> = ArrayList()
) : Parcelable
