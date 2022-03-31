package com.example.attendanceapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapplication.Models.StudentsPandAStats
import com.example.attendanceapplication.R
import com.github.lzyzsd.circleprogress.DonutProgress

class CourseAttendanceAdapter(val context: Context, var arraylist : ArrayList<StudentsPandAStats>) : RecyclerView.Adapter<CourseAttendanceAdapter.CourseViewHolder>() {
    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.courseAttendanceStatsTextView)
        var percent = itemView.findViewById<TextView>(R.id.courseAttendancePercentageTextView)
        var mess = itemView.findViewById<TextView>(R.id.messageTextView)
        var donut = itemView.findViewById<DonutProgress>(R.id.donutProgreebar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_list_view_for_attendancestats,parent,false))
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val temp = arraylist[position]
        holder.name.setText(temp.rollno)
        val p = temp.totPresentee
        val a = temp.totAbsentee
        val tot = p+a
        var per = 0
        if(tot!=0){
            per = (p*100)/(p+a)
        }
        var mess = "Attendance is good!"
        if(per<75){
            mess = "Eligible for W Grade"
        }
        holder.percent.setText("$p / $tot")
        holder.mess.setText(mess)
        holder.donut.progress = per
    }

    override fun getItemCount(): Int {
        return arraylist.size
    }


//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentAttendanceAdapter.StudentAttendanceViewHolder {
//        return StudentAttendanceAdapter.StudentAttendanceViewHolder(
//            LayoutInflater.from(parent.context)
//                .inflate(R.layout.custom_list_view_for_attendancestats, parent, false)
//        )
//    }
//
//    override fun onBindViewHolder(holder: StudentAttendanceAdapter.StudentAttendanceViewHolder, position: Int) {
//        val temp = arraylist[position]
//        holder.name.setText(temp.name)
//        val p = temp.totPresentee
//        val a = temp.totAbsentee
//        val tot = p+a
//        var per = 0
//        if(tot!=0){
//            per = (p*100)/(p+a)
//        }
//        var mess = "Attendance is good!"
//        if(tot==0){
//            mess = "Classes are yet to start"
//        }
//        else if((3*a)>p){
//            val temp = (3*a)-p
//            mess = "Attend $temp more consecutive classes to get 75% mark"
//        }
//        holder.percent.setText("$p / $tot")
//        holder.message.setText(mess)
//        holder.dognut.progress = per
//
//        holder.itemView.setOnClickListener { view->
//            if(!holder.message.text.toString().equals("Classes are yet to start")){
//                Log.d("Samkit","$position clicked")
//                GlobalScope.launch(Dispatchers.IO) {
//                    Log.d("Samkit","Start After Clicking ${Thread.currentThread().name}")
//                    FirebaseFirestore.getInstance()
//                        .collection("Student")
//                        .document(FirebaseAuth.getInstance().currentUser!!.uid)
//                        .collection("CourseOverallStats")
//                        .document(arraylist[position].uid)
//                        .get().await().toObject(CoursePandAStats::class.java)?.let {
//                            Log.d("Samkit","Exists on ${Thread.currentThread().name}")
//                            withContext(Dispatchers.Main){
//                                Log.d("Samkit","After context change ${Thread.currentThread().name}")
//                                val action = StudentAttendanceForCoursesFragmentDirections.actionStudentAttendanceForCoursesFragmentToStudentRecordFragment(it)
//                                Navigation.findNavController(view).navigate(action)
//                            }
//                        }
//
//                }
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return arraylist.size
//    }
}