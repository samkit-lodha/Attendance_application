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
import com.example.attendanceapplication.Dao.FacultyDao
import com.example.attendanceapplication.Models.FacultyUser
import com.example.attendanceapplication.databinding.FragmentFacultyUserAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FacultyUserAddFragment : Fragment() {

    private lateinit var binding : FragmentFacultyUserAddBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var args : FacultyUserAddFragmentArgs
    private var tempDelOrRem : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_faculty_user_add,container,false)

        auth = FirebaseAuth.getInstance()
        args = FacultyUserAddFragmentArgs.fromBundle(requireArguments())
        tempDelOrRem= args.delOrRem

        if(tempDelOrRem == 1){
            binding.createAccountFacultyButton.text = "Create Account"
        }
        else{
            binding.passwordFacultyUserEdittext.visibility = View.GONE
            binding.nameFaacultyUserEditText.visibility = View.GONE
            binding.createAccountFacultyButton.text = "Delete Account"
        }

        binding.createAccountFacultyButton.setOnClickListener {
            createAccount()
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val dp = resources.getStringArray(R.array.Derpartment)
        val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,dp)
        binding.departmentFacultyDropBox.setAdapter(adap)
    }

    private fun createAccount() {
        if(binding.nameFaacultyUserEditText.text.toString().isEmpty() && tempDelOrRem==1){
            binding.nameFaacultyUserEditText.setError("Enter your name")
            binding.nameFaacultyUserEditText.requestFocus()
            return
        }
        else if(binding.emailaddressFacultyUserEdittext.text.toString().isEmpty()){
            binding.emailaddressFacultyUserEdittext.setError("Enter valid email address")
            binding.emailaddressFacultyUserEdittext.requestFocus()
            return
        }
        else if(binding.passwordFacultyUserEdittext.text.length < 5 && tempDelOrRem==1){
            binding.passwordFacultyUserEdittext.setError("Enter valid email address")
            binding.passwordFacultyUserEdittext.requestFocus()
            return
        }


        if(tempDelOrRem==1){
            nextCreateAccount()
        }
        else{
            nextRemoveAccount()
        }
    }

    private fun nextRemoveAccount() {
        FirebaseFirestore.getInstance().collection("Faculty").whereEqualTo("department",binding.departmentFacultyDropBox.text.toString().trim()).get()
            .addOnSuccessListener {
                var m=0
                for(snapshot in it.documents){
                    if(snapshot.get("email")!!.equals(binding.emailaddressFacultyUserEdittext.text.toString().trim())){
                        val tempuid = snapshot.id
                        val facultyDao = FacultyDao()
                        facultyDao.deleteFaculty(tempuid)
                        Toast.makeText(requireContext(),"Deleted successfully",Toast.LENGTH_LONG).show()
                        NavHostFragment.findNavController(this).navigate(UserAddFragmentDirections.actionUserAddFragmentToAdminFeatureFragment())
                        m=1
                    }
                }
                if(m==0){
                    Toast.makeText(requireContext(),"No user's data match with the current information",Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(),"No user's data match with the current information",Toast.LENGTH_LONG).show()
            }
    }

    private fun nextCreateAccount() {
        auth.createUserWithEmailAndPassword(binding.emailaddressFacultyUserEdittext.text.toString().trim(),binding.passwordFacultyUserEdittext.text.toString().trim())
            .addOnSuccessListener {
                val tempuid = it.user!!.uid

                val user = FacultyUser(
                    binding.nameFaacultyUserEditText.text.toString().trim(),
                    binding.emailaddressFacultyUserEdittext.text.toString().trim(),
                    tempuid,
                    "Faculty",
                    binding.departmentFacultyDropBox.text.toString()
                )
                val facultyDao = FacultyDao()
                facultyDao.addFaculty(user)
                Toast.makeText(requireContext(),"Faculty added successfully", Toast.LENGTH_LONG).show()
                NavHostFragment.findNavController(this).navigate(FacultyUserAddFragmentDirections.actionFacultyUserAddFragmentToAdminFeatureFragment())
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"Faculty not registered. Please try again", Toast.LENGTH_LONG).show()
            }
    }
}