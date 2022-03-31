package com.example.attendanceapplication

import android.os.Bundle
import android.util.Log
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
import com.example.attendanceapplication.Models.AttendanceForTheDay
import com.example.attendanceapplication.Models.Course
import com.example.attendanceapplication.databinding.FragmentCourseAttendanceDateSelectionBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class CourseAttendanceDateSelectionFragment : Fragment() {

    private lateinit var binding: FragmentCourseAttendanceDateSelectionBinding
    private lateinit var courseDao: CourseDao
    private var uidlist : ArrayList<String> = ArrayList()
    private var courselist : ArrayList<String> = ArrayList()
    private var datelist : ArrayList<String> = ArrayList()
    private var finUid : String = ""
    private lateinit var job : Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_course_attendance_date_selection,container,false)
        courseDao = CourseDao()


        binding.departmentCourseDateAttendanceDropBox.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                finUid=""
                binding.coursesCourseDateAttendanceDropBox.setText("")
                datelist.clear()
                binding.dateCourseDateAttendanceDropBox.setText("")
                setCursesDropBox(binding.departmentCourseDateAttendanceDropBox.text.toString(),binding.semesterCourseDateAttendanceDropBox.text.toString())
            }

        })

        binding.semesterCourseDateAttendanceDropBox.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                finUid = ""
                binding.coursesCourseDateAttendanceDropBox.setText("")
                datelist.clear()
                binding.dateCourseDateAttendanceDropBox.setText("")
                setCursesDropBox(binding.departmentCourseDateAttendanceDropBox.text.toString(),binding.semesterCourseDateAttendanceDropBox.text.toString())
            }
        })

        binding.coursesCourseDateAttendanceDropBox.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(binding.coursesCourseDateAttendanceDropBox.text.toString().length>0){
                    finUid = uidlist[position]
                    binding.dateCourseDateAttendanceDropBox.setText("")
                    setDateDropBox(position)
                }
            }
        })

        binding.viewAttendanceButton.setOnClickListener {
            if(binding.dateCourseDateAttendanceDropBox.text.toString().length==0){
                binding.dateCourseDateAttendanceDropBox.setError("Please select a date by clicking on date from drop box")
                Toast.makeText(requireContext(),"${binding.dateCourseDateAttendanceDropBox.error}",
                    Toast.LENGTH_LONG).show()
            }
            else if(binding.coursesCourseDateAttendanceDropBox.text.toString().length==0){
                binding.coursesCourseDateAttendanceDropBox.setError("No course selected. Please select a course from dropbox menu")
                Toast.makeText(requireContext(),"No course selected. Please select a course from dropbox menu",
                    Toast.LENGTH_LONG).show()
            }
            else{
                GlobalScope.launch(Dispatchers.Main) {
                    navigate()
                }
            }
        }


        return binding.root
    }

    private suspend fun navigate(){
        Log.d("Samkit","Start After Clicking ${Thread.currentThread().name}")
        FirebaseFirestore.getInstance()
            .collection("Course")
            .document(finUid)
            .collection("AttendanceForTheDay")
            .document(binding.dateCourseDateAttendanceDropBox.text.toString())
            .get().await().toObject(AttendanceForTheDay::class.java)?.let {aFTD->
                Log.d("Samkit","Exists on ${Thread.currentThread().name}")
                withContext(Dispatchers.Main){
                    chaloKhatamKarte(aFTD)
                }
            }
    }

    private fun chaloKhatamKarte(aFTD : AttendanceForTheDay) {
        NavHostFragment.findNavController(this).navigate(CourseAttendanceDateSelectionFragmentDirections.actionCourseAttendanceDateSelectionFragmentToAttendanceViewerFragment(aFTD))
    }

    override fun onResume() {
        super.onResume()
        val dp = resources.getStringArray(R.array.Derpartment)
        val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,dp)
        binding.departmentCourseDateAttendanceDropBox.setAdapter(adap)
        val se = resources.getStringArray(R.array.Semester)
        val adap1 = ArrayAdapter(requireContext(),R.layout.dropbox_view,se)
        binding.semesterCourseDateAttendanceDropBox.setAdapter(adap1)
        setCursesDropBox(binding.departmentCourseDateAttendanceDropBox.text.toString(),binding.semesterCourseDateAttendanceDropBox.text.toString())
        if(binding.coursesCourseDateAttendanceDropBox.text.toString().length>0){
            binding.coursesCourseDateAttendanceDropBox.text.clear()
            binding.dateCourseDateAttendanceDropBox.text.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(job.isActive){
            job.cancel()
        }
    }

    private fun setCursesDropBox(dep: String, sem : String){
        courselist.clear()
        uidlist.clear()
        job = GlobalScope.launch(Dispatchers.IO) {
            courseDao.coursecollection.whereEqualTo("department",dep).whereEqualTo("semester",sem).get().addOnCompleteListener {
                if(!it.result.documents.isEmpty()){
                    for(key in it.result.documents) {
                        val t = key.toObject(Course::class.java)
                        courselist.add(t!!.name)
                        uidlist.add(t.uid)
                    }
                    val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,courselist)
                    binding.coursesCourseDateAttendanceDropBox.setAdapter(adap)
                }
            }
        }
    }
    private fun setDateDropBox(position : Int){
        datelist.clear()
        GlobalScope.launch(Dispatchers.IO) {
            Log.d("Samkit","STARTING : ${Thread.currentThread().name}")
            courseDao.coursecollection.document(uidlist[position])
                .collection("AttendanceForTheDay").get().await()?.let {
                    if(!it.documents.isEmpty()){
                        for(k in it.documents){
                            datelist.add(k.get("date").toString())
                        }
                        withContext(Dispatchers.Main){
                            Log.d("Samkit","Changing context")
                            val adap = ArrayAdapter(requireContext(),R.layout.dropbox_view,datelist)
                            binding.dateCourseDateAttendanceDropBox.setAdapter(adap)
                        }
                    }
                }
        }
    }
}