package com.example.weatherapp.weatherMainRV

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.Utils.Utils
import com.example.weatherapp.WeatherViewModel
import com.example.weatherapp.jsonManager.JsonManager

class WeatherAdapter(private val weatherModelArrayList: WeatherViewModel,
                     private var clickListener: MainActivity
    ) : RecyclerView.Adapter<WeatherAdapter.MyViewHolder>(){

        private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_view_weather_main,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: WeatherModel = weatherModelArrayList.weatherLocationArray[position]

        val sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
        val userUnits = sharedPreferences.getString(MainActivity.USER_UNITS,null)

        holder.weatherImage.setImageResource(model.getImageWeather())
        holder.weatherLocation.text = "" + model.getLocationName()
        holder.weatherDescription.text = model.getDescriptionInfo()

        if(userUnits == "imperial"){
            holder.weatherTemperature.text = String.format("%.2f",Utils.convertCelciusToFahrenheit(model.getTemperature())) + " °F"
        }
        else if(userUnits == "standard"){
            holder.weatherTemperature.text =  String.format("%.2f",Utils.convertCelciusToKelvin(model.getTemperature())) + " K"
        }
        else{
            holder.weatherTemperature.text = model.getTemperature().toString() + " °C"
        }

        holder.weatherHumidity.text = model.getHumidityPercent().toString() + " %"

        if(userUnits == "imperial"){
            holder.weatherWindSpeed.text =  String.format("%.2f",Utils.convertMeterSpeedToMiles(model.getWindSpeed())) + " m/h"
        }
        else{
            holder.weatherWindSpeed.text = model.getWindSpeed().toString() + " m/s"
        }

        // click on item to see more information
        holder.weatherView.setOnClickListener {
            clickListener.clickedItem(model)
            Log.d("DEBUG","Kliknieto w pozycje " + position)
        }

        // clickListener to remove item from array
        holder.weatherView.findViewById<ImageButton>(R.id.remove_item_Btn).setOnClickListener{
            //Toast.makeText(holder.weatherView.context,"Usunięto pozycję numer "+ position,Toast.LENGTH_SHORT).show()
            Log.d("DEBUG","Usunięto pozycje " + holder.adapterPosition)

            //remove file with current and forecast weather data
            try {
                JsonManager.deleteFileFromInternalStorage(context, weatherModelArrayList.weatherLocationArray[holder.adapterPosition].getFilenameCurrentWeather())
                JsonManager.deleteFileFromInternalStorage(context, weatherModelArrayList.weatherLocationArray[holder.adapterPosition].getFilenameForecastWeather())
            }
            catch (e: Exception){
                Log.d("DEBUG","Error when deleting file")
            }

            weatherModelArrayList.weatherLocationArray.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    //return size of arraylist
    override fun getItemCount(): Int {
        return weatherModelArrayList.weatherLocationArray.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val weatherImage: ImageView = itemView.findViewById(R.id.card_view_img_weather)
        val weatherLocation: TextView = itemView.findViewById(R.id.card_view_location)
        val weatherDescription: TextView = itemView.findViewById(R.id.card_view_description)
        val weatherTemperature: TextView = itemView.findViewById(R.id.card_view_temperature)
        val weatherHumidity: TextView = itemView.findViewById(R.id.card_view_humidity_percent)
        val weatherWindSpeed: TextView = itemView.findViewById(R.id.card_view_wind_speed)
        val weatherView = itemView
    }

    // interface to implement click function
    interface ClickListener{
        fun clickedItem(weatherModel: WeatherModel)
    }

}