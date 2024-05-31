package com.example.weatherapp.jsonManager

import android.content.Context
import android.util.Log
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi
import com.example.weatherapp.weatherResponseData.ForecastWeatherResponseApi
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object JsonManager {

    fun saveJsonToInternalStorageCurrentData(context: Context, dataWeather: CurrentWeatherResponseApi, filename: String){

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

    fun readJsonFromInternalStorageCurrentData(context: Context, filename: String): CurrentWeatherResponseApi?{
        val gson = Gson()
        return try {
            val file = File(context.filesDir, filename)
            if (file.exists()) {
                val fileInputStream: FileInputStream = context.openFileInput(filename)
                val inputStreamReader = fileInputStream.bufferedReader()
                val jsonString = inputStreamReader.use { it.readText() }
                fileInputStream.close()
                Log.d("DEBUG", "Read weather data from file $filename")
                gson.fromJson(jsonString, CurrentWeatherResponseApi::class.java)
            } else {
                println("File not found: $filename")
                null
            }
        } catch (e: IOException) {
            println("Error reading data: ${e.message}")
            null
        }
    }

    fun saveJsonToInternalStorageForecastData(context: Context, dataWeather: ForecastWeatherResponseApi, filename: String){

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

    fun readJsonFromInternalStorageForecastData(context: Context, filename: String): ForecastWeatherResponseApi?{
        val gson = Gson()
        return try {
            val file = File(context.filesDir, filename)
            if (file.exists()) {
                val fileInputStream: FileInputStream = context.openFileInput(filename)
                val inputStreamReader = fileInputStream.bufferedReader()
                val jsonString = inputStreamReader.use { it.readText() }
                fileInputStream.close()
                Log.d("DEBUG", "Read weather data from file $filename")
                gson.fromJson(jsonString, ForecastWeatherResponseApi::class.java)
            } else {
                println("File not found: $filename")
                null
            }
        } catch (e: IOException) {
            println("Error reading data: ${e.message}")
            null
        }
    }

    fun deleteFileFromInternalStorage(context: Context, filename: String): Boolean{
        Log.d("DEBUG", "Deleting weather data file $filename")
        return context.deleteFile(filename)
    }
}