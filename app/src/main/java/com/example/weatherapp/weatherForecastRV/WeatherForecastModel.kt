package com.example.weatherapp.weatherForecastRV

import android.health.connect.datatypes.units.Temperature

// data class to display forecast weather information in ForecastRV
data class WeatherForecastModel(
    private var forecastTimeStamp: String,
    private var forecastTemperature: String ){

    fun getForecastTimeStamp(): String{
        return forecastTimeStamp
    }

    fun setForecastTimeStamp(forecastTimeStamp: String){
        this.forecastTimeStamp = forecastTimeStamp
    }

    fun getForecastTemperature(): String{
        return forecastTemperature
    }

    fun setForecastTemperature(forecastTemperature: String){
        this.forecastTemperature = forecastTemperature
    }

}