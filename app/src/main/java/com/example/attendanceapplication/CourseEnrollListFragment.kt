package com.example.attendanceapplication

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapplication.Adapters.CourseAdapter
import com.example.attendanceapplication.Dao.CourseDao
import com.example.attendanceapplication.Models.Course
import com.example.attendanceapplication.databinding.FragmentCourseEnrollListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class CourseEnrollListFragment : Fragment() {

    private lateinit var db : FirebaseFirestore
    private lateinit var binding : FragmentCourseEnrollListBinding
    private lateinit var args: CourseEnrollListFragmentArgs
    private lateinit var adapter : CourseAdapter
    private lateinit var recyclerView: RecyclerView
    private var courseList = ArrayList<Course>()
    private lateinit var courseDao : CourseDao
    private lateinit var progressDialog: ProgressDialog
    private var dep : String = ""
    private var sem : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_course_enroll_list,container,false)

        db = FirebaseFirestore.getInstance()
        args = CourseEnrollListFragmentArgs.fromBundle(requireArguments())
        dep = args.department
        sem = args.semester
        courseDao = CourseDao()
        recyclerView = binding.courseListEnrollRecyclerView

        adapter = CourseAdapter(requireContext(),courseList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        eventChangeListener()

        return binding.root
    }

    private fun eventChangeListener() {
        db.collection("Course").addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error !=null){
                    Toast.makeText(requireContext(),error.message,Toast.LENGTH_LONG).show()
                    return
                }

                if(value != null){
                    for(dc in value.documentChanges){
                        if(dc.type == DocumentChange.Type.ADDED){
                            val temp =dc.document.toObject(Course::class.java)
                            if(temp.semester.equals(sem) && temp.department.equals(dep) && !temp.studentsList.contains(FirebaseAuth.getInstance().currentUser!!.uid)){
                                courseList.add(temp)
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }
}