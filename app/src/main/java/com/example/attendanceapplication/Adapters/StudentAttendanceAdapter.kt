package com.example.attendanceapplication.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapplication.Models.CourseNamePresentAbsent
import com.example.attendanceapplication.Models.CoursePandAStats
import com.example.attendanceapplication.R
import com.example.attendanceapplication.StudentAttendanceForCoursesFragmentDirections
import com.github.lzyzsd.circleprogress.DonutProgress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StudentAttendanceAdapter(val context: Context,var arraylist : ArrayList<CourseNamePresentAbsent>) : RecyclerView.Adapter<StudentAttendanceAdapter.StudentAttendanceViewHolder>() {
    class StudentAttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.courseAttendanceStatsTextView)
        val percent = itemView.findViewById<TextView>(R.id.courseAttendancePercentageTextView)
        val message = itemView.findViewById<TextView>(R.id.messageTextView)
        val dognut = itemView.findViewById<DonutProgress>(R.id.donutProgreebar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttendanceViewHolder {
        return StudentAttendanceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_list_view_for_attendancestats,parent,false))
    }

    override fun onBindViewHolder(holder: StudentAttendanceViewHolder, position: Int) {
        val temp = arraylist[position]
        holder.name.setText(temp.name)
        val p = temp.totPresentee
        val a = temp.totAbsentee
        val tot = p+a
        var per = 0
        if(tot!=0){
            per = (p*100)/(p+a)
        }
        var mess = "Attendance is good!"
        if(tot==0){
            mess = "Classes are yet to start"
        }
        else if((3*a)>p){
            val temp = (3*a)-p
            mess = "Attend $temp more consecutive classes to get 75% mark"
        }
        holder.percent.setText("$p / $tot")
        holder.message.setText(mess)
        holder.dognut.progress = per

        holder.itemView.setOnClickListener { view->
            if(!holder.message.text.toString().equals("Classes are yet to start")){
                Log.d("Samkit","$position clicked")
                GlobalScope.launch(Dispatchers.IO) {
                    Log.d("Samkit","Start After Clicking ${Thread.currentThread().name}")
                    FirebaseFirestore.getInstance()
                    .collection("Student")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .collection("CourseOverallStats")
                    .document(arraylist[position].uid)
                    .get().await().toObject(CoursePandAStats::class.java)?.let {
                            Log.d("Samkit","Exists on ${Thread.currentThread().name}")
                            withContext(Dispatchers.Main){
                                Log.d("Samkit","After context change ${Thread.currentThread().name}")
                                val action = StudentAttendanceForCoursesFragmentDirections.actionStudentAttendanceForCoursesFragmentToStudentRecordFragment(it)
                                Navigation.findNavController(view).navigate(action)
                            }
                        }

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arraylist.size
    }
}