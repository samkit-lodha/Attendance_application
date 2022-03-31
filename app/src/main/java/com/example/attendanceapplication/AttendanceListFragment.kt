package com.example.attendanceapplication

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapplication.Adapters.StudentsListForAttendanceAdapter
import com.example.attendanceapplication.Dao.CourseDao
import com.example.attendanceapplication.Dao.StudentDao
import com.example.attendanceapplication.Models.*
import com.example.attendanceapplication.databinding.FragmentAttendanceListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class AttendanceListFragment : Fragment() {

    private lateinit var binding : FragmentAttendanceListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : StudentsListForAttendanceAdapter
    private lateinit var args : AttendanceListFragmentArgs
    private lateinit var course : Course
    private lateinit var courseDao: CourseDao
    private lateinit var attendanceList : ArrayList<Attendance>
    private lateinit var presentee : ArrayList<String>
    private lateinit var absentee : ArrayList<String>
    private lateinit var docRef : DocumentReference
    private var dep = ""
    private var sem = ""
    private var cou = ""
    private var date = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_attendance_list,container,false)
        course = Course()
        args = AttendanceListFragmentArgs.fromBundle(requireArguments())
        dep = args.department
        sem = args.semester
        cou = args.course
        date = args.date

        courseDao = CourseDao()
        attendanceList = ArrayList()
        presentee = ArrayList()
        absentee = ArrayList()

        recyclerView = binding.studentAttendanceRecyclerView
        adapter = StudentsListForAttendanceAdapter(requireContext(),attendanceList,presentee,absentee)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setup()

        binding.submitAttendanceButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setPositiveButton("YES"){_,_->
                setAttendance()
            }
            builder.setNegativeButton("Cancel"){_,_->

            }
            builder.setTitle("Submit attendance")
            builder.setMessage("Are you sure you want to submit attendance dated for $date and course $cou?")
            builder.create().show()
        }

        return binding.root
    }

    private suspend fun setAttendanceList(){
        val studentDao = StudentDao()
        for(key in course.studentsList){
            val job = GlobalScope.launch(Dispatchers.Main) {
                val temp = studentDao.getStudentById(key).await().toObject(StudentUser::class.java)
                attendanceList.add(Attendance(temp!!.rollno,key,false))
                absentee.add(temp.rollno)
                Log.d("Samkit","${temp.rollno} and ${temp.name}")
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun setup() {

        courseDao.coursecollection.whereEqualTo("department",dep).whereEqualTo("semester",sem).whereEqualTo("name",cou).get().addOnCompleteListener {
            if(it.isSuccessful){
                for(ds in it.result.documents){
                    course = ds.toObject(course::class.java)!!
                    docRef = courseDao.coursecollection.document(course.uid)
                }

                GlobalScope.launch(Dispatchers.Main) {
                    setAttendanceList()
                    withContext(Dispatchers.Main){
                        Log.d("Samkit","Done.....")
                        binding.submitAttendanceButton.text = "Submit Attendance"
                    }
                }
            }
        }
    }

    private fun setAttendance() {
        val calendar = Calendar.getInstance()
        val temp = date + " " + calendar.get(Calendar.HOUR).toString() + " : " + calendar.get(Calendar.MINUTE).toString() + " : " + calendar.get(Calendar.MILLISECOND).toString()+ " : " + "(${calendar.get(Calendar.DAY_OF_WEEK).toString()})"
        courseDao.addAttendanceForTheDay(course, AttendanceForTheDay(temp,presentee,absentee))
        courseDao.updatePresenteeStats(course,presentee)
        courseDao.updateAbsenteeStats(course,absentee)
        val studentDao = StudentDao()
        studentDao.updatePresenteeStat(course,temp,presentee)
        studentDao.updateAbsenteeStat(course,temp,absentee)
        Toast.makeText(requireContext(),"Attendance uploaded for $cou for $temp",Toast.LENGTH_LONG).show()
        NavHostFragment.findNavController(this).navigate(AttendanceListFragmentDirections.actionAttendanceListFragmentToFacultyFeaturesFragment())
    }
}