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
import com.example.attendanceapplication.Models.CoursePandAStats
import com.example.attendanceapplication.databinding.FragmentStudentRecordBinding

class StudentRecordFragment : Fragment() {

    private lateinit var binding : FragmentStudentRecordBinding
    private lateinit var args: StudentRecordFragmentArgs
    private lateinit var cPA : CoursePandAStats
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter : RecordAdapter
    private var alist : ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_student_record,container,false)
        args = StudentRecordFragmentArgs.fromBundle(requireArguments())
        recyclerView = binding.studentRecordRecyclerView
        cPA = args.cpa
        alist.addAll(cPA.datePresentee)

        adapter = RecordAdapter(requireContext(),alist)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.presentStudentTextView.setOnClickListener {
            if(binding.absentStudentLine.isVisible){
                binding.presentStudentLine.visibility = View.VISIBLE
                binding.absentStudentLine.visibility = View.GONE
                alist.clear()
                alist.addAll(cPA.datePresentee)
                adapter.notifyDataSetChanged()
            }
        }

        binding.absentStudentTextView.setOnClickListener {
            if(binding.presentStudentLine.isVisible){
                binding.presentStudentLine.visibility = View.GONE
                binding.absentStudentLine.visibility = View.VISIBLE
                alist.clear()
                alist.addAll(cPA.dateAbsentee)
                adapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }
}