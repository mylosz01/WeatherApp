package com.example.weatherapp.weather_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.Utils.Utils
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

        val dataWeather = arguments?.getSerializable("data") as? CurrentWeatherResponseApi

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
            view.findViewById<TextView>(R.id.timeLocation).text = convertUnixTimeToDateTime(dataWeather.dt!!.toLong())

            //set description
            view.findViewById<TextView>(R.id.weather_description).text = dataWeather.weather?.get(0)?.description.toString()

            //set temperature
            view.findViewById<TextView>(R.id.temperature).text = dataWeather.main?.temp.toString() + " °C"

            //set pressure
            view.findViewById<TextView>(R.id.pressure).text = dataWeather.main?.pressure.toString() + " Pa"
        }

        return view
    }

    fun convertUnixTimeToDateTime(unixTime: Long): String {
        val instant = Instant.ofEpochSecond(unixTime)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return dateTime.format(formatter)
    }

}