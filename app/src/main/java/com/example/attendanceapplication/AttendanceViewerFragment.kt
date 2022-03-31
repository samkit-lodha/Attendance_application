package com.example.attendanceapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapplication.Adapters.RecordAdapter
import com.example.attendanceapplication.Models.AttendanceForTheDay
import com.example.attendanceapplication.databinding.FragmentAttendanceViewerBinding

class AttendanceViewerFragment : Fragment() {

    private lateinit var binding : FragmentAttendanceViewerBinding
    private lateinit var args : AttendanceViewerFragmentArgs
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : RecordAdapter
    private var alist : ArrayList<String> = ArrayList()
    private lateinit var aFTD : AttendanceForTheDay

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_attendance_viewer,container,false)
        args = AttendanceViewerFragmentArgs.fromBundle(requireArguments())
        aFTD = args.aftd
        recyclerView = binding.attendanceRecordRecyclerView

        alist.addAll(aFTD.presentee)

        adapter = RecordAdapter(requireContext(),alist)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.presentStudentAttendanceTextView.setOnClickListener {
            if(binding.absentStudentAttendanceLine.isVisible){
                binding.presentStudentAttendanceLine.visibility = View.VISIBLE
                binding.absentStudentAttendanceLine.visibility = View.GONE
                alist.clear()
                alist.addAll(aFTD.presentee)
                adapter.notifyDataSetChanged()
            }
        }

        binding.absentStudentAttendanceTextView.setOnClickListener {
            if(binding.presentStudentAttendanceLine.isVisible){
                binding.presentStudentAttendanceLine.visibility = View.GONE
                binding.absentStudentAttendanceLine.visibility = View.VISIBLE
                alist.clear()
                alist.addAll(aFTD.absentee)
                adapter.notifyDataSetChanged()
            }
        }
        return binding.root
    }
}