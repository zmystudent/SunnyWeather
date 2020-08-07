package com.example.sunnyweather_kt.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunnyweather_kt.logic.Repository
import com.example.sunnyweather_kt.logic.model.Place

/**
 * 梳理一下这个类的工作流程:
 *      1.首先外部调用该Model的searchPlaces方法时,并不会发生任何请求或函数调用
 *      只会将String(query)值设置到searchLiveData中去.
 *      2.一旦searchLiveData的数据发生变化,那么switchMap方法就会执行,然后在转换函数中调用Repository.searchPlaces(query)获取真正的用户数据
 *      同时switchMap方法会将Repository.searchPlaces(query)返回的LiveData转换成一个可观察的LiveData对象,Activity只要观察这个LiveData就可以了
 */
class PlaceViewModel : ViewModel() {
    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

    private val searchLiveData = MutableLiveData<String>()

    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        //由于我们的LiveData对象,是通过该方法返回的,每次调用都返回一个新的LiveData对象,(无法观察原数据的变化)因此需要使用switchMap函数
        Repository.searchPlaces(query)
    }

    //ViewModel的数据改变在Fragment中
    val placeList = ArrayList<Place>()

    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()
}