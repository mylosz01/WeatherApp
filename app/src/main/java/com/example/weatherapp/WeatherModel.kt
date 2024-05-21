package com.example.weatherapp

// data class to display information in recycleView MainActivity
data class WeatherModel(
    private var locationName: String,
    private var extraInfo: String) {

    fun getLocationName(): String{
        return locationName
    }

    fun setLocationName(location_name:String){
        this.locationName = location_name
    }

    fun getExtraInfo(): String{
        return extraInfo
    }

    fun setExtraInfo(extra_info:String){
        this.extraInfo = extra_info
    }

}