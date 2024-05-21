package com.example.weatherapp.apiManager

import com.example.weatherapp.Utils.Utils
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi
import com.example.weatherapp.weatherResponseData.ForecastWeatherResponseApi
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("/data/2.5/weather?")
    suspend fun getCurrentWeatherData(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") ApiKey: String = Utils.API_KEY,
    ): Response<CurrentWeatherResponseApi>

    @GET("/data/2.5/forecast?")
    suspend fun getForecastWeatherData(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") ApiKey: String = Utils.API_KEY,
    ): Response<ForecastWeatherResponseApi>
}