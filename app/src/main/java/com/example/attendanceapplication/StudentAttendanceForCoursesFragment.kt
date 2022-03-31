package com.example.attendanceapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapplication.Adapters.StudentAttendanceAdapter
import com.example.attendanceapplication.Dao.CourseDao
import com.example.attendanceapplication.Dao.StudentDao
import com.example.attendanceapplication.Models.*
import com.example.attendanceapplication.databinding.FragmentStudentAttendanceForCoursesBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class StudentAttendanceForCoursesFragment : Fragment() {

    private lateinit var binding : FragmentStudentAttendanceForCoursesBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var studentUser : StudentUser
    private lateinit var studentDao: StudentDao
    private lateinit var courseDao: CourseDao
    private lateinit var courseNamePresentAbsent: ArrayList<CourseNamePresentAbsent>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentAttendanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_student_attendance_for_courses,container,false)
        auth = FirebaseAuth.getInstance()
        studentDao = StudentDao()
        courseDao = CourseDao()
        courseNamePresentAbsent = ArrayList()

        recyclerView = binding.courseWithAttendanceRecyclerView
        adapter = StudentAttendanceAdapter(requireContext(),courseNamePresentAbsent)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        GlobalScope.launch{
            Log.d("Samkit","Start ${Thread.currentThread().name}")
            setup()
        }
        return binding.root
    }


    private suspend fun setup() {
        studentUser = first()
        Log.d("Samkit","Starting ${Thread.currentThread().name}")
        val job = GlobalScope.launch{
            for(courseUid in studentUser.courseList){
                GlobalScope.launch(Dispatchers.Main){
                    val temp = second(courseUid)
                    courseNamePresentAbsent.add(temp)
                    adapter.notifyDataSetChanged()
                    Log.d("Samkit","${temp.name} : ${temp.totPresentee} : ${temp.totAbsentee} and thread : ${Thread.currentThread().name}")
                }
            }
        }
    }

    private suspend fun first() : StudentUser{
        return studentDao.getStudentById(auth.currentUser!!.uid).await().toObject(StudentUser::class.java)!!
    }

    private suspend fun second(courseUid:String) : CourseNamePresentAbsent{
        val tempcourse = courseDao.getCourseById(courseUid).await().toObject(Course::class.java)!!
        Log.d("Samkit","${tempcourse.name} ${Thread.currentThread().name}")
        courseDao.getCoursePandAstats(courseUid,studentUser.rollno).await().toObject(StudentsPandAStats::class.java)?.let {
            return CourseNamePresentAbsent(tempcourse.name,tempcourse.uid,it.totPresentee,it.totAbsentee)
        }
        return CourseNamePresentAbsent(tempcourse.name,tempcourse.uid,0,0)
    }
}