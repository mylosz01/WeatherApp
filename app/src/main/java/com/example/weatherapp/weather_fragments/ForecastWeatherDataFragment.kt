package com.example.weatherapp.weather_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.weatherForecastRV.WeatherForecastAdapter
import com.example.weatherapp.weatherForecastRV.WeatherForecastModel
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

        //Adding recycleView
        forecastRecycleView = view.findViewById(R.id.forecast_weather_RV)

        forecastRecycleView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL,false)

        val exampleData = ArrayList<WeatherForecastModel>()

        //string date
        val dateString = "2024-05-22 00:00:00"

        //input format
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        //format input date
        val inputDate = LocalDateTime.parse(dateString,inputFormatter)

        //create formatter for date
        val formatter = DateTimeFormatter.ofPattern("dd MMM, ha")
        //parse date using formatter
        val date = inputDate.format(formatter)

        //add example date
        exampleData.add(WeatherForecastModel(date,1,30.0))
        exampleData.add(WeatherForecastModel(date,2,22.5))
        exampleData.add(WeatherForecastModel(date,2,22.0))
        exampleData.add(WeatherForecastModel(date,3,-11.0))
        exampleData.add(WeatherForecastModel(date,4,5.0))

        forecastRecycleView.adapter = WeatherForecastAdapter(exampleData)

        return view
    }

}