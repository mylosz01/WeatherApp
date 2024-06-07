package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.weatherapp.weather_fragments.AdvancedWeatherDataFragment
import com.example.weatherapp.weather_fragments.BasicWeatherDataFragment
import com.example.weatherapp.weather_fragments.ForecastWeatherDataFragment

class WeatherActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: SwipePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        // get filename of weather data
        val weatherCurrentFilename = intent.getStringExtra("CurrentWeatherData")
        val weatherForecastFilename = intent.getStringExtra("ForecastWeatherData")

        if(weatherCurrentFilename == null){
            Log.d("DEBUG","weatherCurrentData null")
        }
        else{
            Log.d("DEBUG","weatherCurrentData not null")
        }

        val tempBundle = Bundle().apply {
            putString("data",weatherCurrentFilename)
        }

        viewPager = findViewById<ViewPager2>(R.id.viewPagerFragment)

        val fragments = listOf(BasicWeatherDataFragment().apply {arguments = tempBundle},
            AdvancedWeatherDataFragment().apply {arguments = tempBundle},
            ForecastWeatherDataFragment().apply {
                arguments = Bundle().apply {
                    putString("forecast",weatherForecastFilename)
                }
            })

        adapter = SwipePagerAdapter(this,fragments)
        viewPager.adapter = adapter

        /*val basicWeather = BasicWeatherDataFragment().apply {
            arguments = tempBundle
        }

        val advancedWeather = AdvancedWeatherDataFragment().apply {
            arguments = tempBundle
        }

        val forecastWeather = ForecastWeatherDataFragment().apply {
            arguments = Bundle().apply {
                putSerializable("forecast",weatherForecastFilename)
            }
        }

        if(savedInstanceState == null){
            supportFragmentManager.commit {
                replace(R.id.basic_weather_data, basicWeather)
                replace(R.id.advanced_weather_data, advancedWeather)
                replace(R.id.forecast_weather_data, forecastWeather)
            }
        }*/
    }
}