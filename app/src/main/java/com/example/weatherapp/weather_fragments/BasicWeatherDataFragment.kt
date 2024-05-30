package com.example.weatherapp.weather_fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.weatherapp.R
import com.example.weatherapp.WeatherViewModel
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi

class BasicWeatherDataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_basic_weather_data, container, false)

        val dataWeather = arguments?.getSerializable("data") as? CurrentWeatherResponseApi


        if(dataWeather != null){
            Log.d("DEBUG","dataWeather not null")
            //set location name
            view.findViewById<TextView>(R.id.locationName).text = dataWeather.name

            //set latitude name
            view.findViewById<TextView>(R.id.latitude).text = dataWeather.coord!!.lat.toString()

            //set longitude name
            view.findViewById<TextView>(R.id.longitude).text = dataWeather.coord.lon.toString()
        }
        else{
            Log.d("DEBUG","dataWeather null")
        }

        return view
    }

}