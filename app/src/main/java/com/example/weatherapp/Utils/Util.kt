package com.example.weatherapp.Utils

import com.example.weatherapp.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
            // Clouds
            in 801..810 -> R.drawable.cloudy_3
            // Clear
            800 -> R.drawable.sun
            else -> R.drawable.sun
        }
    }

    fun convertUnixTimeToDateTime(unixTime: Long): String {
        val instant = Instant.ofEpochSecond(unixTime)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return dateTime.format(formatter)
    }

    fun convertCelciusToKelvin(temperature : Double): Double{
        return temperature + 273.15
    }

    fun convertCelciusToFahrenheit(temperature : Double): Double{
        return temperature * 9/5 + 32
    }

    fun convertMeterSpeedToMiles(metersPerSecond: Double): Double {
        val milesPerHour = metersPerSecond * 2.23694
        return milesPerHour
    }

}