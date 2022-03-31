package com.example.attendanceapplication.Dao

import com.example.attendanceapplication.Models.AttendanceForTheDay
import com.example.attendanceapplication.Models.Course
import com.example.attendanceapplication.Models.StudentUser
import com.example.attendanceapplication.Models.StudentsPandAStats
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CourseDao {
    val db = FirebaseFirestore.getInstance()
    val coursecollection = db.collection("Course")
    val auth = FirebaseAuth.getInstance()

    fun addCourse(course: Course){
        course?.let {
            GlobalScope.launch(Dispatchers.IO) {
                val tempuid = coursecollection.document().id
                course.uid = tempuid
                coursecollection.document(tempuid).set(it)
            }
        }
    }

    fun addAttendanceForTheDay(course : Course,attendanceForTheDay: AttendanceForTheDay){
        attendanceForTheDay?.let {
            GlobalScope.launch(Dispatchers.IO) {
                coursecollection.document(course.uid).collection("AttendanceForTheDay").document(attendanceForTheDay.date).set(attendanceForTheDay)
            }
        }
    }

    fun updatePresenteeStats(course: Course,presentee : ArrayList<String>){
        for(roll in presentee){
            GlobalScope.launch {
                coursecollection.document(course.uid).collection("StudentsOverallStats").document(roll).get().addOnCompleteListener {
                    if(it.result.exists()){
                        val temp = it.result.toObject(StudentsPandAStats::class.java)
                        coursecollection.document(course.uid).collection("StudentsOverallStats").document(roll).set(StudentsPandAStats(temp!!.rollno,temp.totPresentee+1,temp.totAbsentee))
                    }else{
                        coursecollection.document(course.uid).collection("StudentsOverallStats").document(roll).set(StudentsPandAStats(roll,1,0))
                    }
                }
            }
        }
    }

    fun updateAbsenteeStats(course: Course,absentee : ArrayList<String>){
        for(roll in absentee){
            GlobalScope.launch {
                coursecollection.document(course.uid).collection("StudentsOverallStats").document(roll).get().addOnCompleteListener {
                    if(it.result.exists()){
                        val temp = it.result.toObject(StudentsPandAStats::class.java)
                        coursecollection.document(course.uid).collection("StudentsOverallStats").document(roll).set(StudentsPandAStats(temp!!.rollno,temp.totPresentee,temp.totAbsentee+1))
                    }else{
                        coursecollection.document(course.uid).collection("StudentsOverallStats").document(roll).set(StudentsPandAStats(roll,0,1))
                    }
                }
            }
        }
    }

    fun getCourseById(uid : String) : Task<DocumentSnapshot>{
        return coursecollection.document(uid).get()
    }

    fun getCoursePandAstats(courseUid : String,rollno : String) : Task<DocumentSnapshot>{
        return coursecollection.document(courseUid).collection("StudentsOverallStats").document(rollno).get()
    }

    fun updateStudent(uid : String){
        GlobalScope.launch {
            val studentDao = StudentDao()
            val currentuserid = auth.currentUser!!.uid

            val course = getCourseById(uid).await().toObject(Course::class.java)!!
            val user = studentDao.getStudentById(currentuserid).await().toObject(StudentUser::class.java)!!
            val isStudent = course.studentsList.contains(currentuserid)

            if(isStudent){
                course.studentsList.remove(currentuserid)
                user.courseList.remove(uid)
            }
            else {
                course.studentsList.add(currentuserid)
                user.courseList.add(uid)
            }

            coursecollection.document(uid).set(course)
            studentDao.studentcollection.document(currentuserid).set(user)
        }
    }
}

