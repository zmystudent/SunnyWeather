package com.example.sunnyweather_kt.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather_kt.logic.network.SunnyWeatherNetwork
import com.example.sunnyweather_kt.logic.model.Place
import kotlinx.coroutines.Dispatchers
import java.lang.Exception

object Repository {
    //liveData()函数可以自动构建并返回一个LiveData对象,然后在它的代码块中提供一个挂起函数的上下文
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                //包装数据
                Result.success(places)
            } else {
                //包装异常
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        /**
         * 将包装的结果发射出去.
         * 这个emit()方法类似于调用setValue()方法来通知数据变化,只不过这里我们无法直接取得返回的LiveData对象,
         * 因此系统提供了这个方法
         */
        emit(result)
    }
}