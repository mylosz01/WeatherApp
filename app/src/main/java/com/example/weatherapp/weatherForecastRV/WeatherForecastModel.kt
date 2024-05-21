package com.example.weatherapp.weatherForecastRV

import java.time.LocalDate

// data class to display forecast weather information in ForecastRV
data class WeatherForecastModel(
    private var forecastTimeStamp: LocalDate,
    private var forecastImageInt: Int,
    private var forecastTemperature: Double ){

    fun getForecastTimeStamp(): LocalDate{
        return forecastTimeStamp
    }

    fun setForecastTimeStamp(forecastTimeStamp: LocalDate){
        this.forecastTimeStamp = forecastTimeStamp
    }

    fun getForecastImage(): Int{
        return forecastImageInt
    }

    fun setForecastImage(forecastImageInt: Int){
        this.forecastImageInt = forecastImageInt
    }

    fun getForecastTemperature(): Double{
        return forecastTemperature
    }

    fun setForecastTemperature(forecastTemperature: Double){
        this.forecastTemperature = forecastTemperature
    }

}