package com.example.attendanceapplication.Dao

import com.example.attendanceapplication.Models.Course
import com.example.attendanceapplication.Models.CoursePandAStats
import com.example.attendanceapplication.Models.StudentUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StudentDao {
    val db = FirebaseFirestore.getInstance()
    val studentcollection = db.collection("Student")
    val auth = FirebaseAuth.getInstance()

    fun addStudent(studentUser : StudentUser){
        studentUser?.let {
            GlobalScope.launch(Dispatchers.IO){
                studentcollection.document(studentUser.uid).set(it)
            }
        }
    }

    fun updatePresenteeStat(course: Course,date : String,presentee : ArrayList<String>){
        val courseDao = CourseDao()
        var temppresentee = ArrayList<String>()
        var tempabsenttee = ArrayList<String>()

        for(roll in presentee){
            GlobalScope.launch {
                studentcollection.whereEqualTo("rollno",roll).get().addOnCompleteListener {
                    for(dc in it.result.documents){
                        if(dc.exists()){
                            var user = dc.toObject(StudentUser::class.java)
                            studentcollection.document(user!!.uid).collection("CourseOverallStats").document(course.uid).get().addOnCompleteListener {
                                if(it.result.exists()){
                                    val temp = it.result.toObject(CoursePandAStats::class.java)
                                    temppresentee = temp!!.datePresentee
                                    temppresentee.add(date)
                                    tempabsenttee = temp.dateAbsentee
                                    studentcollection.document(user!!.uid).collection("CourseOverallStats").document(course.uid).set(CoursePandAStats(course.uid,course.name,temppresentee,tempabsenttee))
                                }
                                else{
                                    temppresentee.clear()
                                    temppresentee.add(date)
                                    tempabsenttee.clear()
                                    studentcollection.document(user!!.uid).collection("CourseOverallStats").document(course.uid).set(CoursePandAStats(course.uid,course.name,temppresentee,tempabsenttee))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateAbsenteeStat(course: Course,date : String,absentee : ArrayList<String>){
        var temppresentee = ArrayList<String>()
        var tempabsenttee = ArrayList<String>()

        for(roll in absentee){
            GlobalScope.launch {
                studentcollection.whereEqualTo("rollno",roll).get().addOnCompleteListener {
                    for(dc in it.result.documents){
                        if(dc.exists()){
                            var user = dc.toObject(StudentUser::class.java)
                            studentcollection.document(user!!.uid).collection("CourseOverallStats").document(course.uid).get().addOnCompleteListener {
                                if(it.result.exists()){
                                    val temp = it.result.toObject(CoursePandAStats::class.java)
                                    temppresentee = temp!!.datePresentee
                                    tempabsenttee = temp.dateAbsentee
                                    tempabsenttee.add(date)
                                    studentcollection.document(user!!.uid).collection("CourseOverallStats").document(course.uid).set(CoursePandAStats(course.uid,course.name,temppresentee,tempabsenttee))
                                }
                                else{
                                    temppresentee.clear()
                                    tempabsenttee.clear()
                                    tempabsenttee.add(date)
                                    studentcollection.document(user!!.uid).collection("CourseOverallStats").document(course.uid).set(CoursePandAStats(course.uid,course.name,temppresentee,tempabsenttee))
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    fun getStudentById(uid : String) : Task<DocumentSnapshot> {
        return studentcollection.document(uid).get()
    }

    fun deleteStudent(uid : String){
        GlobalScope.launch(Dispatchers.IO){
            studentcollection.document(uid).delete()
        }
    }
}