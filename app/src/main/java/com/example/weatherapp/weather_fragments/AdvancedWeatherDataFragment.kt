package com.example.weatherapp.weather_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.weatherapp.R
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi

class AdvancedWeatherDataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_advanced_weather_data, container, false)

        val dataWeather = arguments?.getSerializable("data") as? CurrentWeatherResponseApi

        if(dataWeather != null){
            //set rain percent
            view.findViewById<TextView>(R.id.rain_percent).text = (dataWeather.rain?.h.toString().toDoubleOrNull() ?: 0.0).toString()

            //set wind speed
            view.findViewById<TextView>(R.id.wind_speed).text = "${dataWeather.wind?.speed} m/s"

            //set humidity
            view.findViewById<TextView>(R.id.humidity).text = "${dataWeather.main?.humidity} %"

            //set visibility
            view.findViewById<TextView>(R.id.visibility).text = "${dataWeather.visibility} m"

            //rotate image
            view.findViewById<ImageView>(R.id.wind_direction_sign).rotation = dataWeather.wind?.deg!!.toFloat() + 90

            //set wind degree
            view.findViewById<TextView>(R.id.wind_degree).text = "${dataWeather.wind?.deg} °"

            //set cloudiness
            view.findViewById<TextView>(R.id.cloudiness).text = "${dataWeather.clouds?.all} %"
        }

        return view
    }
}