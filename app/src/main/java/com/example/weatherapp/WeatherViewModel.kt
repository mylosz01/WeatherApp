package com.example.weatherapp

import androidx.lifecycle.ViewModel
import com.example.weatherapp.weatherMainRV.WeatherModel

class WeatherViewModel : ViewModel() {

    val weatherLocationArray: ArrayList<WeatherModel> = ArrayList()

}