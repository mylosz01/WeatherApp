package com.example.weatherapp.weatherMainRV

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R

class WeatherAdapter(private val weatherModelArrayList: ArrayList<WeatherModel>,
                     private var clickListener: MainActivity
    ) : RecyclerView.Adapter<WeatherAdapter.MyViewHolder>(){

        private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card_view_weather_main,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model: WeatherModel = weatherModelArrayList[position]

        holder.weatherLocation.text = "" + model.getLocationName()
        holder.weatherDescription.text = model.getDescriptionInfo()
        holder.weatherTemperature.text = model.getTemperature().toString() + "°"
        holder.weatherRainPercent.text = model.getRainPercent().toString() + " %"
        holder.weatherWindSpeed.text = model.getWindSpeed().toString() + " m/s"

        // click on item to see more information
        holder.weatherView.setOnClickListener {
            clickListener.clickedItem(model)
            Log.d("DEBUG","Kliknieto w pozycje " + position)
        }

        // clickListener to remove item from array
        holder.weatherView.findViewById<ImageButton>(R.id.remove_item_Btn).setOnClickListener{
            Toast.makeText(holder.weatherView.context,"Usunięto pozycję numer "+ position,Toast.LENGTH_SHORT).show()
            Log.d("DEBUG","Usunięto pozycje " + position)
            weatherModelArrayList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    //return size of arraylist
    override fun getItemCount(): Int {
        return weatherModelArrayList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val weatherLocation: TextView = itemView.findViewById(R.id.card_view_location)
        val weatherDescription: TextView = itemView.findViewById(R.id.card_view_description)
        val weatherTemperature: TextView = itemView.findViewById(R.id.card_view_temperature)
        val weatherRainPercent: TextView = itemView.findViewById(R.id.card_view_rain_percent)
        val weatherWindSpeed: TextView = itemView.findViewById(R.id.card_view_wind_speed)
        val weatherView = itemView
    }

    // interface to implement click function
    interface ClickListener{
        fun clickedItem(weatherModel: WeatherModel)
    }

}