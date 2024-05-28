package com.example.weatherapp.jsonManager

import android.content.Context
import android.text.BoringLayout
import android.util.Log
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi
import com.google.gson.Gson
import java.io.FileOutputStream
import java.io.IOException

object JsonManager {

    fun saveJsonToInternalStorage(context: Context, dataWeather: CurrentWeatherResponseApi, filename: String){

        val gson = Gson()
        val jsonString = gson.toJson(dataWeather)

        try{
            val fileOutputStream: FileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            fileOutputStream.write(jsonString.toByteArray())
            fileOutputStream.close()
            Log.d("DEBUG","JSON saved to $filename")
        }
        catch (e: IOException){
            Log.d("DEBUG","Error saving JSON : ${e.message}")
        }

    }

    fun readJsonFromInternalStorage(){
        
    }

    fun deleteFileFromInternalStorage(context: Context, filename: String): Boolean{
        Log.d("DEBUG", "Deleting weather data file $filename")
        return context.deleteFile(filename)
    }
}