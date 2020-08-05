package com.example.sunnyweather_kt.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    private const val BASE_URL = "https://api.caiyunapp.com/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    //泛型的实化,已经知道了泛型的具体类型,然后调用上面的create()方法,实际上调用的retrofit.create()
    inline fun <reified T> create(): T = create(T::class.java)
}