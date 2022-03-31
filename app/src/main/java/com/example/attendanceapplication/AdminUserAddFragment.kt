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
import com.example.attendanceapplication.Dao.AdminDao
import com.example.attendanceapplication.Models.AdminUser
import com.example.attendanceapplication.databinding.FragmentAdminUserAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminUserAddFragment : Fragment() {

    private lateinit var binding : FragmentAdminUserAddBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var args : AdminUserAddFragmentArgs
    private var tempDelOrRem : Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_admin_user_add,container,false)

        auth = FirebaseAuth.getInstance()
        args = AdminUserAddFragmentArgs.fromBundle(requireArguments())
        tempDelOrRem = args.delOrRem

        if(tempDelOrRem == 1){
            binding.createAccountAdminButton.text = "Create Account"
        }
        else{
            binding.passwordAdminUserEdittext.visibility = View.GONE
            binding.nameAdminUserEditText.visibility = View.GONE
            binding.createAccountAdminButton.text = "Delete Account"
        }

        binding.createAccountAdminButton.setOnClickListener {
            createAccount()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val dp = resources.getStringArray(R.array.Derpartment)
        val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,dp)
        binding.departmentAdminDropBox.setAdapter(adap)
    }

    private fun createAccount() {
        if(binding.nameAdminUserEditText.text.toString().isEmpty() && tempDelOrRem==1){
            binding.nameAdminUserEditText.setError("Enter your name")
            binding.nameAdminUserEditText.requestFocus()
            return
        }
        else if(binding.emailaddressAdminUserEdittext.text.toString().isEmpty()){
            binding.emailaddressAdminUserEdittext.setError("Enter valid email address")
            binding.emailaddressAdminUserEdittext.requestFocus()
            return
        }
        else if(binding.passwordAdminUserEdittext.text.length < 5 && tempDelOrRem==1){
            binding.passwordAdminUserEdittext.setError("Enter valid email address")
            binding.passwordAdminUserEdittext.requestFocus()
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
        FirebaseFirestore.getInstance().collection("Admin").whereEqualTo("department",binding.departmentAdminDropBox.text.toString().trim()).get()
            .addOnSuccessListener {
                var m=0
                for(snapshot in it.documents){
                if(snapshot.get("email")!!.equals(binding.emailaddressAdminUserEdittext.text.toString().trim())){
                    val tempuid = snapshot.id
                    val adminDao = AdminDao()
                    adminDao.deleteAdmin(tempuid)
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
        auth.createUserWithEmailAndPassword(binding.emailaddressAdminUserEdittext.text.toString().trim(),binding.passwordAdminUserEdittext.text.toString().trim())
            .addOnSuccessListener {
                val tempuid = it.user!!.uid

                val user = AdminUser(
                    binding.nameAdminUserEditText.text.toString().trim(),
                    binding.emailaddressAdminUserEdittext.text.toString().trim(),
                    tempuid,
                    "Admin",
                    binding.departmentAdminDropBox.text.toString()
                )
                val adminDao = AdminDao()
                adminDao.addAdmin(user)
                Toast.makeText(requireContext(),"Admin added successfully", Toast.LENGTH_LONG).show()
                NavHostFragment.findNavController(this).navigate(AdminUserAddFragmentDirections.actionAdminUserAddFragmentToAdminFeatureFragment())
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"User not registered. Please try again", Toast.LENGTH_LONG).show()
            }
    }
}