package com.example.attendanceapplication.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CoursePandAStats(
    val uid : String = "",
    val name : String = "",
    val datePresentee : ArrayList<String> = ArrayList(),
    val dateAbsentee : ArrayList<String> = ArrayList(),
) : Parcelable