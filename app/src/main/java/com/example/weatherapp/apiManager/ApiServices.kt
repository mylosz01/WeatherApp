package com.example.weatherapp.apiManager

import com.example.weatherapp.CitySuggestion
import com.example.weatherapp.Utils.Utils
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi
import com.example.weatherapp.weatherResponseData.ForecastWeatherResponseApi
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("/data/2.5/weather?")
    suspend fun getCurrentWeatherData(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("lat") latitude: Double = 0.0,
        @Query("lon") longitude: Double = 0.0,
        @Query("appid") apiKey: String = Utils.API_KEY,
    ): Response<CurrentWeatherResponseApi>

    @GET("/data/2.5/forecast?")
    suspend fun getForecastWeatherData(
        @Query("q") city: String,
        @Query("units") units: String = "metric",
        @Query("lat") latitude: Double = 0.0,
        @Query("lon") longitude: Double = 0.0,
        @Query("cityID") cityID: Int = 0,
        @Query("cnt") cnt: Int = 0,
        @Query("appid") apiKey: String = Utils.API_KEY,
    ): Response<ForecastWeatherResponseApi>

    @GET("geo/1.0/direct")
    suspend fun getCitySuggestions(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String = Utils.API_KEY,
    ): Response<List<CitySuggestion>>
}