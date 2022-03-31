package com.example.attendanceapplication.Dao

import com.example.attendanceapplication.Models.AdminUser
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AdminDao {
    val db = FirebaseFirestore.getInstance()
    val admincollection = db.collection("Admin")

    fun addAdmin(user : AdminUser){
        user?.let {
            GlobalScope.launch(Dispatchers.IO){
                admincollection.document(user.uid).set(it)
            }
        }
    }

    fun getAdminById(uid : String) : Task<DocumentSnapshot>{
        return admincollection.document(uid).get()
    }

    fun deleteAdmin(uid : String){
        GlobalScope.launch(Dispatchers.IO){
            admincollection.document(uid).delete()
        }
    }
}