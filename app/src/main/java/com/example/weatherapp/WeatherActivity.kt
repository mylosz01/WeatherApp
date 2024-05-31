package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi
import com.example.weatherapp.weatherResponseData.ForecastWeatherResponseApi
import com.example.weatherapp.weather_fragments.AdvancedWeatherDataFragment
import com.example.weatherapp.weather_fragments.BasicWeatherDataFragment
import com.example.weatherapp.weather_fragments.ForecastWeatherDataFragment

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val weatherCurrentData = intent.getSerializableExtra("CurrentWeatherData") as? CurrentWeatherResponseApi
        val weatherForecastData = intent.getSerializableExtra("ForecastWeatherData") as? ForecastWeatherResponseApi

        if(weatherCurrentData == null){
            Log.d("DEBUG","weatherCurrentData null")
        }
        else{
            Log.d("DEBUG","weatherCurrentData not null")
        }

        val tempBundle = Bundle().apply {
            putSerializable("data",weatherCurrentData)
        }

        val basicWeather = BasicWeatherDataFragment().apply {
            arguments = tempBundle
        }

        val advancedWeather = AdvancedWeatherDataFragment().apply {
            arguments = tempBundle
        }

        val forecastWeather = ForecastWeatherDataFragment().apply {
            arguments = Bundle().apply {
                putSerializable("forecast",weatherForecastData)
            }
        }

        if(savedInstanceState == null){
            supportFragmentManager.commit {
                replace(R.id.basic_weather_data, basicWeather)
                replace(R.id.advanced_weather_data, advancedWeather)
                replace(R.id.forecast_weather_data, forecastWeather)
            }
        }
    }
}