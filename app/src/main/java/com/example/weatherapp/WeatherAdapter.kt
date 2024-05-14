package com.example.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class WeatherAdapter(private val weatherModelArrayList: ArrayList<WeatherModel>):
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.card_view_weather_main,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: WeatherModel = weatherModelArrayList[position]
        holder.weatherLocation.setText("" + model.getLocationName())
        holder.weatherInfo.setText(model.getExtraInfo())
        holder.weatherView.setOnClickListener{
            Toast.makeText(holder.weatherView.context,"Kliknięto w pozycję numer "+ position,Toast.LENGTH_SHORT).show()
        }
        holder.weatherView.findViewById<ImageButton>(R.id.remove_item_Btn).setOnClickListener{
            Toast.makeText(holder.weatherView.context,"Usunięto pozycję numer "+ position,Toast.LENGTH_SHORT).show()
            weatherModelArrayList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return weatherModelArrayList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val weatherLocation: TextView = itemView.findViewById(R.id.card_view_location)
        val weatherInfo: TextView = itemView.findViewById(R.id.card_view_extra_info)
        val weatherView = itemView
    }

}