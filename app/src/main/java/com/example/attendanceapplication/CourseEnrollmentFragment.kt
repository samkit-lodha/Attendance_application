package com.example.attendanceapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.attendanceapplication.databinding.FragmentCourseEnrollmentBinding

class CourseEnrollmentFragment : Fragment() {

    private lateinit var binding: FragmentCourseEnrollmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_course_enrollment,container,false)

        binding.viewCourseEnrollButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(CourseEnrollmentFragmentDirections.actionCourseEnrollmentFragmentToCourseEnrollListFragment(binding.departmentCourseEnrollDropBox.text.toString(),binding.semesterCourseEnrollDropBox.text.toString()))
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val dp = resources.getStringArray(R.array.Derpartment)
        val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,dp)
        binding.departmentCourseEnrollDropBox.setAdapter(adap)
        val se = resources.getStringArray(R.array.Semester)
        val adap1 = ArrayAdapter(requireContext(),R.layout.dropbox_view,se)
        binding.semesterCourseEnrollDropBox.setAdapter(adap1)
    }
}