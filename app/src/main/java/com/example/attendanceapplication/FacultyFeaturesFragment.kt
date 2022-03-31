package com.example.attendanceapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.attendanceapplication.databinding.FragmentFacultyFeaturesBinding
import com.google.firebase.auth.FirebaseAuth


class FacultyFeaturesFragment : Fragment() {

    private lateinit var binding : FragmentFacultyFeaturesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_faculty_features,container,false)

        binding.addCourseFacultyFeaturesButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(FacultyFeaturesFragmentDirections.actionFacultyFeaturesFragmentToAddCourseFragment())
        }
        binding.attendanceFacultyFeaturesButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(FacultyFeaturesFragmentDirections.actionFacultyFeaturesFragmentToAttendanceDataSetterFragment())
        }

        binding.attendanceStatsFacultyFeaturesButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(FacultyFeaturesFragmentDirections.actionFacultyFeaturesFragmentToCourseAttendanceDateSelectionFragment())
        }

        binding.overallAttendanceStatsFacultyFeaturesButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(FacultyFeaturesFragmentDirections.actionFacultyFeaturesFragmentToCourseOverallDateSetterFragment())
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.my_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.sign_out_option_toolbar){
            signOutMenu()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signOutMenu() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_ ->
            FirebaseAuth.getInstance().signOut()
            NavHostFragment.findNavController(this).navigate(FacultyFeaturesFragmentDirections.actionFacultyFeaturesFragmentToLoginFragment())
        }
        builder.setNegativeButton("No"){_,_ ->

        }
        builder.setTitle("Sign out")
        builder.setMessage("Are you sure you want to sign out?")
        builder.create().show()
    }

}