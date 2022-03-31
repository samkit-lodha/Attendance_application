package com.example.attendanceapplication

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapplication.Adapters.CourseAttendanceAdapter
import com.example.attendanceapplication.Dao.CourseDao
import com.example.attendanceapplication.Models.Course
import com.example.attendanceapplication.Models.JsonObject
import com.example.attendanceapplication.Models.PServices
import com.example.attendanceapplication.Models.StudentsPandAStats
import com.example.attendanceapplication.databinding.FragmentOverallStatsBinding
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class OverallStatsFragment : Fragment() {

    private lateinit var binding: FragmentOverallStatsBinding
    private lateinit var args : OverallStatsFragmentArgs
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:CourseAttendanceAdapter
    private lateinit var courseDao: CourseDao
    private var courseUid : String= ""
    private var alist : ArrayList<StudentsPandAStats> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_overall_stats,container,false)
        args = OverallStatsFragmentArgs.fromBundle(requireArguments())
        courseUid = args.uidFin
        courseDao = CourseDao()

        recyclerView = binding.courseOverallWithAttendanceRecyclerView
        adapter = CourseAttendanceAdapter(requireContext(),alist)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        GlobalScope.launch {
            setup()
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private suspend fun setup() {
        courseDao.coursecollection.document(courseUid).collection("StudentsOverallStats").get().await()?.let {
            if(!it.documents.isEmpty()){
                Log.d("Samkit","Starting : ${Thread.currentThread().name}")
                for(ds in it.documents){
                    GlobalScope.launch(Dispatchers.Main) {

                        Log.d("Samkit","object creation : ${Thread.currentThread().name}")
                        ds.toObject(StudentsPandAStats::class.java)?.let {
                            alist.add(it)
                            Log.d("Samkit","Adding  ${it.rollno} -> ${it.totPresentee} : ${it.totAbsentee} ${Thread.currentThread().name}")
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.my_download_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.download_option_toolbar){
            downloadMenu()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun downloadMenu() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_ ->
            GlobalScope.launch {
                val jsonObject = JsonObject()
                val course = courseDao.getCourseById(courseUid).await().toObject(Course::class.java)
                jsonObject.department = course!!.department
                jsonObject.semester = course.semester
//                jsonObject.name = course.name
                jsonObject.name = "4th Time"
                jsonObject.statslist = alist

                val gson = Gson()
                val jsonStr = gson.toJson(jsonObject)

                Log.d("Samkit",jsonStr)

                try {
                    val res = PServices.pInterface.sendJson(jsonObject)
                    Log.d("Samkit",res.status)
                    Log.d("Samkit",res.status)
                }catch (e : Exception){
                    Log.d("Samkit",e.message + " Done")
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        builder.setNegativeButton("No"){_,_ ->

        }
        builder.setTitle("Download overall Stats...")
        builder.setMessage("Are you sure you want to download the pdf?")
        builder.create().show()
    }
}