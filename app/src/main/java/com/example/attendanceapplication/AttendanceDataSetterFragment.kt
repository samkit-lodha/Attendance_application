package com.example.attendanceapplication

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.attendanceapplication.Dao.CourseDao
import com.example.attendanceapplication.databinding.FragmentAttendanceDataSetterBinding
import java.util.*
import kotlin.collections.ArrayList

class AttendanceDataSetterFragment : Fragment() {

    private lateinit var binding : FragmentAttendanceDataSetterBinding
    private lateinit var courseDao: CourseDao
    private var courselist : ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_attendance_data_setter,container,false)
        courseDao = CourseDao()

        binding.departmenttakeAttendanceDropBox.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.coursesTakeAttendanceDropBox.setText("")
                setCursesDropBox(binding.departmenttakeAttendanceDropBox.text.toString(),binding.semesterTakeAttendanceDropBox.text.toString())
            }

        })

        binding.semesterTakeAttendanceDropBox.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.coursesTakeAttendanceDropBox.setText("")
                setCursesDropBox(binding.departmenttakeAttendanceDropBox.text.toString(),binding.semesterTakeAttendanceDropBox.text.toString())
            }
        })

        binding.datePickerActions.setOnClickListener{
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                var temp = dayOfMonth.toString()
                temp += "-"
                temp += ((month+1).toString())
                temp += "-"
                temp += (year.toString())
                binding.dateSelecterAttendanceButton.setText(temp)
            }
            DatePickerDialog(requireContext(),datePickerDialog,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show()

        }

        binding.takeAttendanceButton.setOnClickListener {
            if(binding.dateSelecterAttendanceButton.text.toString().length==0){
                binding.dateSelecterAttendanceButton.setError("Please Select A date by clicking on the calendar icon")
                Toast.makeText(requireContext(),"${binding.dateSelecterAttendanceButton.error}",Toast.LENGTH_LONG).show()
            }
            else if(binding.coursesTakeAttendanceDropBox.text.toString().length==0){
                binding.coursesTakeAttendanceDropBox.setError("No course selected. Please select a course from dropbox menu")
                Toast.makeText(requireContext(),"No course selected. Please select a course from dropbox menu",Toast.LENGTH_LONG).show()
            }
            else{
                NavHostFragment.findNavController(this).navigate(AttendanceDataSetterFragmentDirections.actionAttendanceDataSetterFragmentToAttendanceListFragment(
                    binding.departmenttakeAttendanceDropBox.text.toString(),
                    binding.semesterTakeAttendanceDropBox.text.toString(),
                    binding.coursesTakeAttendanceDropBox.text.toString(),
                    binding.dateSelecterAttendanceButton.text.toString()
                ))
            }
        }

        return binding.root
    }

    override fun onResume() {
        if(binding.coursesTakeAttendanceDropBox.text.toString().length>0){
            binding.coursesTakeAttendanceDropBox.text.clear()
            binding.dateSelecterAttendanceButton.text = ""
        }
        super.onResume()
        val dp = resources.getStringArray(R.array.Derpartment)
        val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,dp)
        binding.departmenttakeAttendanceDropBox.setAdapter(adap)
        val se = resources.getStringArray(R.array.Semester)
        val adap1 = ArrayAdapter(requireContext(),R.layout.dropbox_view,se)
        binding.semesterTakeAttendanceDropBox.setAdapter(adap1)
        setCursesDropBox(binding.departmenttakeAttendanceDropBox.text.toString(),binding.semesterTakeAttendanceDropBox.text.toString())
    }

    private fun setCursesDropBox(dep: String, sem : String){
        courselist.clear()
        courseDao.coursecollection.whereEqualTo("department",dep).whereEqualTo("semester",sem).get().addOnCompleteListener {
            if(it.isSuccessful){
                for(ds in it.result.documents){
                    val temp = ds.get("name").toString()
                    courselist.add(temp)
                }

                val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,courselist)
                binding.coursesTakeAttendanceDropBox.setAdapter(adap)
            }
        }
    }
}