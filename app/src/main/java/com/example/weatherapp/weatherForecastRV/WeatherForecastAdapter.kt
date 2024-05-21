package com.example.weatherapp.weatherForecastRV

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R

class WeatherForecastAdapter(private val weatherForecastModelArrayList: ArrayList<WeatherForecastModel>
    ) : RecyclerView.Adapter<WeatherForecastAdapter.MyForecastViewHolder>(){

        private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyForecastViewHolder {
        context = parent.context
        return MyForecastViewHolder(LayoutInflater.from(context).inflate(R.layout.card_view_weather_forecast_fragment,parent,false))
    }

    override fun onBindViewHolder(holder: MyForecastViewHolder, position: Int) {
        val model: WeatherForecastModel = weatherForecastModelArrayList[position]

        holder.weatherForecastTemperature.text = model.getForecastTemperature()
        holder.weatherForecastTimeStamp.text = model.getForecastTimeStamp()
    }

    //return size of arraylist
    override fun getItemCount(): Int {
        return weatherForecastModelArrayList.size
    }

    class MyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val weatherForecastTimeStamp: TextView = itemView.findViewById(R.id.card_view_forecast_time)
        val weatherForecastTemperature: TextView = itemView.findViewById(R.id.card_view_forecast_temperature)
        val weatherView = itemView
    }
}