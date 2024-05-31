package com.example.weatherapp.weather_fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.Utils.Utils
import com.example.weatherapp.weatherForecastRV.WeatherForecastAdapter
import com.example.weatherapp.weatherForecastRV.WeatherForecastModel
import com.example.weatherapp.weatherResponseData.ForecastWeatherResponseApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ForecastWeatherDataFragment : Fragment() {

    private lateinit var forecastRecycleView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_forecast_weather_data, container, false)

        val dataWeather = arguments?.getSerializable("forecast") as? ForecastWeatherResponseApi

        if(dataWeather != null) {
            Log.d("DEBUG","FORECAST DATA RECEIVE")
            //Adding recycleView
            forecastRecycleView = view.findViewById(R.id.forecast_weather_RV)
            forecastRecycleView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL,false)

            val forecastDataArray = ArrayList<WeatherForecastModel>()

            //input format
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            //create formatter for date
            val formatter = DateTimeFormatter.ofPattern("dd MMM, ha")

            for(forecastData in dataWeather.list!!){

                val dateForecast : String = forecastData.dtTxt.toString()

                //Log.d("DEBUG","FORECAST TIME $dateForecast")

                //format input date
                val inputDate = LocalDateTime.parse(dateForecast,inputFormatter)

                //parse date using formatter
                val date = inputDate.format(formatter)

                // create object ForecastModel to store info
                val forecastModel = WeatherForecastModel(
                    forecastTimeStamp = date,
                    forecastImageInt = Utils.getWeatherImageResource(forecastData.weather?.get(0)?.id!!.toInt()),
                    forecastTemperature = forecastData.main?.temp!!.toDouble()
                )

                forecastDataArray.add(forecastModel)
            }

            forecastRecycleView.adapter = WeatherForecastAdapter(forecastDataArray)
        }

        return view
    }

}