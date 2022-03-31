package com.example.attendanceapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapplication.Models.Attendance
import com.example.attendanceapplication.R

class StudentsListForAttendanceAdapter(var context : Context,var studentList : ArrayList<Attendance>,var presentees : ArrayList<String>,var absentees : ArrayList<String>) : RecyclerView.Adapter<StudentsListForAttendanceAdapter.StudentsListForAttendanceViewHolder>(){

    class StudentsListForAttendanceViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.courseTextView)
        val checkBox = itemView.findViewById<CheckBox>(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentsListForAttendanceViewHolder {
        return StudentsListForAttendanceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.courses_list_view,parent,false))
    }

    override fun onBindViewHolder(holder: StudentsListForAttendanceViewHolder, position: Int) {
        val attendee = studentList[position]
        holder.name.text = attendee.rollno

        holder.checkBox.setOnClickListener {
            if(holder.checkBox.isChecked){
                presentees.add(attendee.rollno)
                absentees.remove(attendee.rollno)
                Toast.makeText(context,"${holder.name.text} is checked",Toast.LENGTH_LONG).show()
            }
            else{
                presentees.remove(attendee.rollno)
                absentees.add(attendee.rollno)
                Toast.makeText(context,"${holder.name.text} is not checked",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return studentList.size
    }
}