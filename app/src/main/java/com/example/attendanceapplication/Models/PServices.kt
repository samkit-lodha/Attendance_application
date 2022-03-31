package com.example.attendanceapplication.Models

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

const val BASE_URL = "https://attendance-pdf-backend.herokuapp.com/"

interface PInterface{
    @POST("create")
    suspend fun sendJson(@Body jsonObject: JsonObject) : ApiResponse
}

object PServices {
    val pInterface : PInterface
    init{
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        pInterface = retrofit.create(PInterface::class.java)
    }
}