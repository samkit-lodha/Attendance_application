package com.example.attendanceapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.example.attendanceapplication.Dao.CourseDao
import com.example.attendanceapplication.Models.Course
import com.example.attendanceapplication.databinding.FragmentCourseOverallDateSetterBinding

class CourseOverallDateSetterFragment : Fragment() {

    private lateinit var binding : FragmentCourseOverallDateSetterBinding
    private lateinit var courseDao: CourseDao
    private var courselist : ArrayList<String> = ArrayList()
    private var uidlist : ArrayList<String> = ArrayList()
    private var uidFin = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_course_overall_date_setter,container,false)
        courseDao = CourseDao()

        binding.departmenttakeOverallAttendanceDropBox.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.coursesTakeOverallAttendanceDropBox.setText("")
                setCursesDropBox(binding.departmenttakeOverallAttendanceDropBox.text.toString(),binding.semesterTakeOverallAttendanceDropBox.text.toString())
            }

        })

        binding.semesterTakeOverallAttendanceDropBox.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.coursesTakeOverallAttendanceDropBox.setText("")
                setCursesDropBox(binding.departmenttakeOverallAttendanceDropBox.text.toString(),binding.semesterTakeOverallAttendanceDropBox.text.toString())
            }
        })

        binding.coursesTakeOverallAttendanceDropBox.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                uidFin = uidlist[position]
            }

        })

        binding.takeOverallAttendanceButton.setOnClickListener {
            if(binding.coursesTakeOverallAttendanceDropBox.text.toString().length>0){
                NavHostFragment.findNavController(this).navigate(CourseOverallDateSetterFragmentDirections.actionCourseOverallDateSetterFragmentToOverallStatsFragment(uidFin))
            }
        }

        return binding.root
    }


    override fun onResume() {
        if(binding.coursesTakeOverallAttendanceDropBox.text.toString().length>0){
            binding.coursesTakeOverallAttendanceDropBox.text.clear()
        }
        super.onResume()
        val dp = resources.getStringArray(R.array.Derpartment)
        val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,dp)
        binding.departmenttakeOverallAttendanceDropBox.setAdapter(adap)
        val se = resources.getStringArray(R.array.Semester)
        val adap1 = ArrayAdapter(requireContext(),R.layout.dropbox_view,se)
        binding.semesterTakeOverallAttendanceDropBox.setAdapter(adap1)
        setCursesDropBox(binding.departmenttakeOverallAttendanceDropBox.text.toString(),binding.semesterTakeOverallAttendanceDropBox.text.toString())
    }

    private fun setCursesDropBox(dep: String, sem : String){
        courselist.clear()
        uidlist.clear()

        courseDao.coursecollection.whereEqualTo("department",dep).whereEqualTo("semester",sem).get().addOnCompleteListener {
            if(it.isSuccessful){
                for(ds in it.result.documents){
                    val temp = ds.toObject(Course::class.java)
                    courselist.add(temp!!.name)
                    uidlist.add(temp.uid)
                }

                val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,courselist)
                binding.coursesTakeOverallAttendanceDropBox.setAdapter(adap)
            }
        }
    }

}