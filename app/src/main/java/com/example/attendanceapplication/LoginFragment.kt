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
import com.example.attendanceapplication.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login,container,false)
        auth = FirebaseAuth.getInstance()
        binding.signinButton.setOnClickListener {
            signin()
        }

        return binding.root
    }

    private fun signin() {
        FirebaseFirestore.getInstance()
            .collection(binding.privilegeDropBox.text.toString().trim())
            .whereEqualTo("email", binding.emailAddressLoginEdittext.text.toString().trim())
            .get()
            .addOnSuccessListener {
                for (snapshot in it.documents) {
                    auth.signInWithEmailAndPassword(
                        binding.emailAddressLoginEdittext.text.toString().trim(),
                        binding.passwordLoginEdittext.text.toString().trim()
                    )
                        .addOnSuccessListener {
                            if (binding.privilegeDropBox.text.toString().trim()
                                    .equals("Admin")
                            ) {
                                Toast.makeText(
                                    requireContext(),
                                    "Admin successfully signed in.",
                                    Toast.LENGTH_LONG
                                ).show()
                                NavHostFragment.findNavController(this)
                                    .navigate(LoginFragmentDirections.actionLoginFragmentToAdminFeatureFragment())
                            } else if (binding.privilegeDropBox.text.toString().trim()
                                    .equals("Faculty")
                            ) {
                                Toast.makeText(
                                    requireContext(),
                                    "Faculty successfully signed in.",
                                    Toast.LENGTH_LONG
                                ).show()
                                NavHostFragment.findNavController(this)
                                    .navigate(LoginFragmentDirections.actionLoginFragmentToFacultyFeaturesFragment())
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Student successfully signed in.",
                                    Toast.LENGTH_LONG
                                ).show()
                                NavHostFragment.findNavController(this)
                                    .navigate(LoginFragmentDirections.actionLoginFragmentToStudentFeatureFragment())
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "There was some technical issue. Please retry!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Theres is no user with the given information.PLease enter the fields again.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    override fun onResume() {
        super.onResume()
        val pr = resources.getStringArray(R.array.Privilege)
        val ar = ArrayAdapter(requireContext(),R.layout.dropbox_view,pr)
        binding.privilegeDropBox.setAdapter(ar)
    }
}
