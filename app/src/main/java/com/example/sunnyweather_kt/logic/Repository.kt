package com.example.sunnyweather_kt.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather_kt.logic.dao.PlaceDao
import com.example.sunnyweather_kt.logic.network.SunnyWeatherNetwork
import com.example.sunnyweather_kt.logic.model.Place
import com.example.sunnyweather_kt.logic.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.Exception
import kotlin.coroutines.CoroutineContext

object Repository {
    //liveData()函数可以自动构建并返回一个LiveData对象,然后在它的代码块中提供一个挂起函数的上下文
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        //这里其实没必要使用coroutineScope创建协程作用域
        coroutineScope {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                //包装数据
                Result.success(places)
            } else {
                //包装异常
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        //这里使用coroutineScope这个函数其实就是为了创建一个协程作用域,后续才能使用async函数
        coroutineScope {
            val deferredRealtime = async { SunnyWeatherNetwork.getRealtimeWeather(lng, lat) }
            val deferredDaily = async { SunnyWeatherNetwork.getDailyWeather(lng, lat) }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather =
                    Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status} " +
                                "+ daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    /**
     * 这是一个按照liveData()函数的参数标准定义的一个高阶函数,在fire函数内部会先调用一下liveData函数,然后统一进行try catch处理
     * 注意: 在liveData()函数的代码块中,是拥有挂起函数啥下文的,可是当回调到Lambda表达式中,代码就没有挂起函数上下文了
     *      因此在函数类型前声明一个suspend关键字
     */
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                //Lambda表达式的回调位置
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            /**
             * 将包装的结果发射出去.
             * 这个emit()方法类似于调用setValue()方法来通知数据变化,只不过这里我们无法直接取得返回的LiveData对象,
             * 因此系统提供了这个方法
             */
            emit(result)
        }
}