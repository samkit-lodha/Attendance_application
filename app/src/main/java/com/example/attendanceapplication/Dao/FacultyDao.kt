package com.example.attendanceapplication.Dao

import com.example.attendanceapplication.Models.FacultyUser
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FacultyDao {
    val db = FirebaseFirestore.getInstance()
    val facultycollection = db.collection("Faculty")

    fun addFaculty(user : FacultyUser){
        user?.let {
            GlobalScope.launch(Dispatchers.IO){
                facultycollection.document(user.uid).set(it)
            }
        }
    }

    fun getFacultyById(uid : String) : Task<DocumentSnapshot> {
        return facultycollection.document(uid).get()
    }

    fun deleteFaculty(uid : String){
        GlobalScope.launch(Dispatchers.IO){
            facultycollection.document(uid).delete()
        }
    }
}