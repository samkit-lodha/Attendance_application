package com.example.attendanceapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.attendanceapplication.databinding.FragmentAdminFeatureBinding
import com.google.firebase.auth.FirebaseAuth

class AdminFeatureFragment : Fragment() {

    private lateinit var binding : FragmentAdminFeatureBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_feature, container, false)

        binding.addAdminButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(AdminFeatureFragmentDirections.actionAdminFeatureFragmentToAdminUserAddFragment(1))
        }
        binding.addStudentButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(AdminFeatureFragmentDirections.actionAdminFeatureFragmentToUserAddFragment(1))
        }
        binding.addFacultyButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(AdminFeatureFragmentDirections.actionAdminFeatureFragmentToFacultyUserAddFragment(1))
        }
        binding.removeAdminButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(AdminFeatureFragmentDirections.actionAdminFeatureFragmentToAdminUserAddFragment(2))
        }
        binding.removeStudentButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(AdminFeatureFragmentDirections.actionAdminFeatureFragmentToUserAddFragment(2))
        }
        binding.removeFacultyButton.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(AdminFeatureFragmentDirections.actionAdminFeatureFragmentToFacultyUserAddFragment(2))
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
            NavHostFragment.findNavController(this).navigate(AdminFeatureFragmentDirections.actionAdminFeatureFragmentToLoginFragment())
        }
        builder.setNegativeButton("No"){_,_ ->

        }
        builder.setTitle("Sign out")
        builder.setMessage("Are you sure you want to sign out?")
        builder.create().show()
    }
}