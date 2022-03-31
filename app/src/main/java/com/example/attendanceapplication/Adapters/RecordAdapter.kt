package com.example.attendanceapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendanceapplication.R

class RecordAdapter(var context: Context,var alist : ArrayList<String>) :RecyclerView.Adapter<RecordAdapter.RecordViewHoler>() {
    class RecordViewHoler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.recordNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHoler {
        return RecordViewHoler(LayoutInflater.from(parent.context).inflate(R.layout.custom_record_list,parent,false))
    }

    override fun onBindViewHolder(holder: RecordViewHoler, position: Int) {
        holder.name.setText(alist[position])
    }

    override fun getItemCount(): Int {
        return alist.size
    }
}