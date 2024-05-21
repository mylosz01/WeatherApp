package com.example.weatherapp.weatherForecastRV

// data class to display forecast weather information in ForecastRV
data class WeatherForecastModel(
    private var forecastTimeStamp: String,
    private var forecastImageInt: Int,
    private var forecastTemperature: Double ){

    fun getForecastTimeStamp(): String{
        return forecastTimeStamp
    }

    fun setForecastTimeStamp(forecastTimeStamp: String){
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