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

        exampleData.add(WeatherForecastModel("2 am","30"))
        exampleData.add(WeatherForecastModel("4 pm","20"))
        exampleData.add(WeatherForecastModel("5 am","10"))
        exampleData.add(WeatherForecastModel("7 am","10"))
        exampleData.add(WeatherForecastModel("8 am","5"))

        forecastRecycleView.adapter = WeatherForecastAdapter(exampleData)

        return view
    }

}