package com.example.attendanceapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapplication.Dao.CourseDao
import com.example.attendanceapplication.Models.Course
import com.example.attendanceapplication.R

class CourseAdapter(var context : Context,var courselist : ArrayList<Course>) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>(){

    class CourseViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        val text = itemview.findViewById<TextView>(R.id.courseTextView)
        val checkBox = itemview.findViewById<CheckBox>(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.courses_list_view,parent,false))
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courselist[position]
        holder.text.setText(course.name)

        holder.checkBox.setOnClickListener {
            CourseDao().updateStudent(course.uid)
        }
    }

    override fun getItemCount(): Int {
        return courselist.size
    }

}