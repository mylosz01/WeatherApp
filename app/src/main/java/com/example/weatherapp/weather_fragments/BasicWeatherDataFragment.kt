package com.example.weatherapp.weather_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.Utils.Utils
import com.example.weatherapp.jsonManager.JsonManager
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class BasicWeatherDataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_basic_weather_data, container, false)

        val dataWeatherFilename = arguments?.getString("data")

        Log.d("DEBUG","CHECK $dataWeatherFilename")

        if(dataWeatherFilename != null){
            // read file with weather data
            val dataWeather = JsonManager.readJsonFromInternalStorageCurrentData(requireContext(),dataWeatherFilename)

            val sharedPreferences = context?.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
            val userUnits = sharedPreferences?.getString(MainActivity.USER_UNITS,"metric")

            if(dataWeather != null){
                //set location name
                view.findViewById<TextView>(R.id.locationName).text = dataWeather.name

                //set image weather
                view.findViewById<ImageView>(R.id.weather_image).setImageResource(Utils.getWeatherImageResource(
                    dataWeather.weather!![0]?.id!!.toInt()))

                //set latitude name
                view.findViewById<TextView>(R.id.latitude).text = "Lat: ${dataWeather.coord!!.lat.toString()}"

                //set longitude name
                view.findViewById<TextView>(R.id.longitude).text = "Lon: ${dataWeather.coord.lon.toString()}"

                //set location time
                view.findViewById<TextView>(R.id.timeLocation).text = Utils.convertUnixTimeToDateTime(dataWeather.dt!!.toLong())

                //set description
                view.findViewById<TextView>(R.id.weather_description).text = dataWeather.weather.get(0)?.description.toString()

                //set temperature
                val temperatureTextView = view.findViewById<TextView>(R.id.temperature)

                if(userUnits == "imperial"){
                    temperatureTextView.text = String.format("%.2f",Utils.convertCelciusToFahrenheit(dataWeather.main?.temp!!)) + " °F"
                }
                else if(userUnits == "standard"){
                    temperatureTextView.text =  String.format("%.2f",Utils.convertCelciusToKelvin(dataWeather.main?.temp!!)) + " K"
                }
                else{
                    temperatureTextView.text = dataWeather.main?.temp.toString() + " °C"
                }

                //set pressure
                view.findViewById<TextView>(R.id.pressure).text = dataWeather.main?.pressure.toString() + " hPa"
            }
        }

        return view
    }



}