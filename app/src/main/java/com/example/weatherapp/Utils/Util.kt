package com.example.weatherapp.Utils

import com.example.weatherapp.R

object Utils {
    const val BASE_URL = "https://api.openweathermap.org"
    const val API_KEY = "d00906ae38034eea1fd96baab88d0757"

    fun getWeatherImageResource(weatherId: Int = -1) : Int{
        return when (weatherId) {
            // Thunderstorm
            in 200..240 -> R.drawable.storm
            // Drizzle
            in 300..340 -> R.drawable.cloudy_sunny
            // Rain
            in 500..540 -> R.drawable.rainy
            // Snow
            in 600..640 -> R.drawable.snowy
            // Atmosphere
            in 700..790 -> R.drawable.cloudy
            // Clear
            800 -> R.drawable.sun
            // Clouds
            in 801..810 -> R.drawable.cloudy_3
            else -> R.drawable.sun
        }
    }
}