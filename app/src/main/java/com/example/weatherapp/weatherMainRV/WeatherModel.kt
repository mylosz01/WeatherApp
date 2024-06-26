package com.example.weatherapp.weatherMainRV
import com.example.weatherapp.R
import com.example.weatherapp.Utils.Utils

// data class to display information in recycleView MainActivity
data class WeatherModel(
    private var imageWeatherId: Int = R.drawable.rainy,
    private var locationName: String = "-",
    private var descriptionInfo: String = "-",
    private var temperature: Double = 0.0,
    private var humidityPercent: Double = 0.0,
    private var windSpeed: Double = 0.0,
    private var filenameCurrentWeather: String = "",
    private var filenameForecastWeather: String = "") {

    fun getFilenameCurrentWeather():String{
        return this.filenameCurrentWeather
    }

    fun getFilenameForecastWeather():String{
        return this.filenameForecastWeather
    }

    fun getImageWeather(): Int{
        return this.imageWeatherId
    }

    fun setImageWeather(idImage: Int){
        this.imageWeatherId = Utils.getWeatherImageResource(idImage)
    }

    fun getLocationName(): String{
        return this.locationName
    }

    fun setLocationName(locationName:String){
        this.locationName = locationName
    }

    fun getDescriptionInfo(): String{
        return this.descriptionInfo
    }

    fun setDescriptionInfo(descriptionInfo:String){
        this.descriptionInfo = descriptionInfo
    }

    fun getTemperature(): Double{
        return this.temperature
    }

    fun setTemperature(temperature: Double){
        this.temperature = temperature
    }

    fun getHumidityPercent(): Double{
        return this.humidityPercent
    }

    fun setHumidityPercent(rainPercent: Double){
        this.humidityPercent = rainPercent
    }

    fun getWindSpeed(): Double{
        return this.windSpeed
    }

    fun setWindSpeed(windSpeed: Double){
        this.windSpeed = windSpeed
    }

}