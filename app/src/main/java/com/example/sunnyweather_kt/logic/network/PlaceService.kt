package com.example.sunnyweather_kt.logic.network

import com.example.sunnyweather_kt.MyApplication
import com.example.sunnyweather_kt.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    @GET("v2/place?token=${MyApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>
}