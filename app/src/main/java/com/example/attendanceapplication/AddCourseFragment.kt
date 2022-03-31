package com.example.attendanceapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.attendanceapplication.Dao.CourseDao
import com.example.attendanceapplication.Models.Course
import com.example.attendanceapplication.databinding.FragmentAddCourseBinding

class AddCourseFragment : Fragment() {

    private lateinit var binding : FragmentAddCourseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_course,container,false)

        binding.addCourseButton.setOnClickListener {
            addTheGivenCourse()
        }

        return binding.root
    }

    private fun addTheGivenCourse() {
        if(binding.courseNameEditText.text.toString().isEmpty()){
            binding.courseNameEditText.setError("Please provide a proper course name")
            binding.courseNameEditText.requestFocus()
            return
        }

        val course = Course(
            binding.departmentCourseDropBox.text.toString(),
            binding.semesterCourseDropBox.text.toString(),
            binding.courseNameEditText.text.toString()
        )
        val courseDao = CourseDao()
        courseDao.addCourse(course)
        Toast.makeText(requireContext(),"Course added successfully",Toast.LENGTH_LONG).show()
        NavHostFragment.findNavController(this).navigate(AddCourseFragmentDirections.actionAddCourseFragmentToFacultyFeaturesFragment())
    }

    override fun onResume() {
        super.onResume()
        val dp = resources.getStringArray(R.array.Derpartment)
        val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,dp)
        binding.departmentCourseDropBox.setAdapter(adap)
        val se = resources.getStringArray(R.array.Semester)
        val adap1 = ArrayAdapter(requireContext(),R.layout.dropbox_view,se)
        binding.semesterCourseDropBox.setAdapter(adap1)
    }

}