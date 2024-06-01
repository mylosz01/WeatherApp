package com.example.weatherapp.weatherForecastRV

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.Utils.Utils

class WeatherForecastAdapter(private val weatherForecastModelArrayList: ArrayList<WeatherForecastModel>
    ) : RecyclerView.Adapter<WeatherForecastAdapter.MyForecastViewHolder>(){

        private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyForecastViewHolder {
        context = parent.context
        return MyForecastViewHolder(LayoutInflater.from(context).inflate(R.layout.card_view_weather_forecast_fragment,parent,false))
    }

    override fun onBindViewHolder(holder: MyForecastViewHolder, position: Int) {
        val model: WeatherForecastModel = weatherForecastModelArrayList[position]

        val sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val userUnits = sharedPreferences.getString(MainActivity.USER_UNITS,null)

        if(userUnits == "imperial"){
            holder.weatherForecastTemperature.text = String.format("%.2f", Utils.convertCelciusToFahrenheit(model.getForecastTemperature())) + " °F"
        }
        else if(userUnits == "standard"){
            holder.weatherForecastTemperature.text =  String.format("%.2f", Utils.convertCelciusToKelvin(model.getForecastTemperature())) + " K"
        }
        else{
            holder.weatherForecastTemperature.text = model.getForecastTemperature().toString() + " °C"
        }
        holder.weatherForecastTimeStamp.text =  model.getForecastTimeStamp()
        holder.weatherForecastImage.setImageResource(model.getForecastImage())
    }

    //return size of arraylist
    override fun getItemCount(): Int {
        return weatherForecastModelArrayList.size
    }

    class MyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val weatherForecastTimeStamp: TextView = itemView.findViewById(R.id.card_view_forecast_time)
        val weatherForecastTemperature: TextView = itemView.findViewById(R.id.card_view_forecast_temperature)
        val weatherForecastImage: ImageView = itemView.findViewById(R.id.card_view_img_weather)
    }
}