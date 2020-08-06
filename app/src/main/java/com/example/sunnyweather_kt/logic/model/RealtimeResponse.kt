package com.example.sunnyweather_kt.logic.model

import com.google.gson.annotations.SerializedName

//实时天气返回的JSON数据解析结构
data class RealtimeResponse(val status: String, val result: Result) {
    data class Result(val realtime: Realtime)

    data class Realtime(
        val skycon: String, val temperature: Float,
        @SerializedName("air_quality") val airQuality: AirQuality
    )

    data class AirQuality(val aqi: AQI)

    data class AQI(val chn: Float)
}