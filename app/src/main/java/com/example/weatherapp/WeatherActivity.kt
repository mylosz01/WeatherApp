package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi
import com.example.weatherapp.weather_fragments.AdvancedWeatherDataFragment
import com.example.weatherapp.weather_fragments.BasicWeatherDataFragment
import com.example.weatherapp.weather_fragments.ForecastWeatherDataFragment

class WeatherActivity : AppCompatActivity() {

    private val weatherViewModel : WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        Log.d("DEBUG"," CREATE WEATHER ACTIVITY")

        val weatherCurrentData = intent.getSerializableExtra("LocationData") as? CurrentWeatherResponseApi

        if(weatherCurrentData == null){
            Log.d("DEBUG","weatherCurrentData null")
        }
        else{
            Log.d("DEBUG","weatherCurrentData not null")
        }

        val basicWeather = BasicWeatherDataFragment().apply {
            arguments = Bundle().apply {
                putSerializable("data",weatherCurrentData)
            }
        }

        /*val advancedWeather = AdvancedWeatherDataFragment().apply {
            arguments = Bundle().apply {
                putSerializable("data",weatherCurrentData)
            }
        }

        val forecastWeather = ForecastWeatherDataFragment().apply {
            arguments = Bundle().apply {
                putSerializable("data",weatherCurrentData)
            }
        }*/

        Log.d("DEBUG"," testing : ${weatherCurrentData?.id}" )

        if(savedInstanceState == null){
            supportFragmentManager.commit {
                replace(R.id.basic_weather_data, basicWeather)
                //replace(R.id.advanced_weather_data, advancedWeather)
                //replace(R.id.forecast_weather_data, forecastWeather)
            }
        }
    }
}