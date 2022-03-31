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
import com.example.attendanceapplication.Dao.StudentDao
import com.example.attendanceapplication.Models.StudentUser
import com.example.attendanceapplication.databinding.FragmentUserAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserAddFragment : Fragment() {

    private lateinit var binding : FragmentUserAddBinding
    private lateinit var args: UserAddFragmentArgs
    private lateinit var auth : FirebaseAuth
    private var tempDelOrRem : Int?=1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_user_add,container,false)

        auth = FirebaseAuth.getInstance()

        args = UserAddFragmentArgs.fromBundle(requireArguments())
        tempDelOrRem = args.delOrRem

        if(tempDelOrRem == 1){
            binding.createAccountButton.text = "Create Account"
        }
        else{
            binding.passwordUserEdittext.visibility = View.GONE
            binding.nameUserEditText.visibility = View.GONE
            binding.createAccountButton.text = "Delete Account"
        }

        binding.createAccountButton.setOnClickListener {
            createAccount()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val dp = resources.getStringArray(R.array.Derpartment)
        val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,dp)
        binding.departmentDropBox.setAdapter(adap)
    }

    private fun createAccount() {
        if(binding.nameUserEditText.text.toString().isEmpty() && tempDelOrRem==1){
            binding.nameUserEditText.setError("Enter your name")
            binding.nameUserEditText.requestFocus()
            return
        }
        else if(binding.emailaddressUserEdittext.text.toString().isEmpty()){
            binding.emailaddressUserEdittext.setError("Enter valid email address")
            binding.emailaddressUserEdittext.requestFocus()
            return
        }
        else if(binding.rollNoUserEdittext.text.toString().trim().isEmpty()){
            binding.rollNoUserEdittext.setError("Enter valid rollNo without any spaces")
            binding.rollNoUserEdittext.requestFocus()
            return
        }
        else if(checkForSpaces(binding.rollNoUserEdittext.text.toString().trim())){
            binding.rollNoUserEdittext.setError("Enter valid rollNo without any spaces")
            binding.rollNoUserEdittext.requestFocus()
            return
        }
        else if(binding.passwordUserEdittext.text.length < 5 && tempDelOrRem==1){
            binding.passwordUserEdittext.setError("Enter valid email address")
            binding.passwordUserEdittext.requestFocus()
            return
        }


        if(tempDelOrRem==1){
            nextCreateAccount()
        }
        else{
            nextRemoveAccount()
        }
    }

    private fun checkForSpaces(str: String): Boolean {
        for(c in str){
            if(c.isWhitespace()){
                return true
            }
        }
        return false
    }

    private fun nextRemoveAccount() {
        FirebaseFirestore.getInstance().collection("Student").whereEqualTo("department",binding.departmentDropBox.text.toString().trim()).get().addOnSuccessListener {
            var m=0
            for(snapshot in it.documents){
                if(snapshot.get("rollno")!!.equals(binding.rollNoUserEdittext.text.toString().trim())){
                    val tempuid = snapshot.id
                    val studentDao = StudentDao()
                    studentDao.deleteStudent(tempuid)
                    Toast.makeText(requireContext(),"Deleted successfully",Toast.LENGTH_LONG).show()
                    NavHostFragment.findNavController(this).navigate(UserAddFragmentDirections.actionUserAddFragmentToAdminFeatureFragment())
                    m=1
                }
            }
            if(m==0)
            {
                Toast.makeText(requireContext(),"No user's data match with the current information",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"No user's data match with the current information",Toast.LENGTH_LONG).show()
        }
    }

    private fun nextCreateAccount() {

        auth.createUserWithEmailAndPassword(binding.emailaddressUserEdittext.text.toString().trim(),binding.passwordUserEdittext.text.toString().trim())
            .addOnSuccessListener {
                val tempuid = it.user!!.uid
                val courseList = ArrayList<String>()
                val user = StudentUser(
                    binding.nameUserEditText.text.toString().trim(),
                    binding.emailaddressUserEdittext.text.toString().trim(),
                    binding.rollNoUserEdittext.text.toString().trim(),
                    tempuid,
                    "Student",
                    binding.departmentDropBox.text.toString(),
                    courseList = courseList
                )
                val studentDao = StudentDao()
                studentDao.addStudent(user)
                Toast.makeText(requireContext(),"Student added successfully",Toast.LENGTH_LONG).show()
                NavHostFragment.findNavController(this).navigate(UserAddFragmentDirections.actionUserAddFragmentToAdminFeatureFragment())
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"User not registered. Please try again",Toast.LENGTH_LONG).show()
            }
    }

}