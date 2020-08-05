package com.example.sunnyweather_kt.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object SunnyWeatherNetwork {
    //private val placeService2 = ServiceCreator.create(PlaceService::class.java)
    private val placeService = ServiceCreator.create<PlaceService>()

    /**
     * suspend: 让该函数变为挂起函数
     */
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()

    /**
     * P471. suspendCoroutine函数的解释
     *
     * Retrofit网络请求完毕会返回一个Call对象,利用扩展函数,对Call进行封装,简化以后回调的写法
     */
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}