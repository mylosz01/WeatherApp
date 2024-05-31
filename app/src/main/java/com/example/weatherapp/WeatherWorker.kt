package com.example.weatherapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weatherapp.Utils.RetrofitInstance
import com.example.weatherapp.jsonManager.JsonManager
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

class WeatherWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext,workerParams) {

    override fun doWork(): Result {
        return try {
            fetchRefreshWeatherData()
            scheduleNextWork()
            Result.success()
        }
        catch (e: Exception){
            Result.retry()
        }
    }

    private fun getFileCurrentDataList(): List<String> {
        val fileList = applicationContext.filesDir.listFiles { _, name ->
            name.startsWith("weather_current_") && name.endsWith(".json")
        }
        return fileList?.map { it.name } ?: emptyList()
    }

    private fun fetchRefreshWeatherData() {

        Log.d("DEBUG","REFRESH DATA BY SCHEDULE")

        // get file with current data weather
        val fileListCurrentData = getFileCurrentDataList()

        // if list is not empty fetch data from weather api
        if(fileListCurrentData.isNotEmpty()){

            for(filenameLocation: String in fileListCurrentData){

                val readWeatherData : CurrentWeatherResponseApi? = JsonManager.readJsonFromInternalStorageCurrentData(
                    applicationContext,filenameLocation)

                // delete current old data
                JsonManager.deleteFileFromInternalStorage(
                    context = applicationContext,
                    filename = filenameLocation)

                // delete forecast old data
                JsonManager.deleteFileFromInternalStorage(
                    context = applicationContext,
                    filename = filenameLocation.replace("current","forecast"))

                if(readWeatherData != null){

                    GlobalScope.launch(Dispatchers.IO) {

                        //fetch current weather data
                        val responseCurrent = try{
                            RetrofitInstance.api.getCurrentWeatherData(
                                city = readWeatherData.name.toString(),
                                units = "metric",
                                latitude = readWeatherData.coord?.lat!!.toDouble(),
                                longitude = readWeatherData.coord.lon!!.toDouble()
                            )
                        }catch (e : IOException){
                            Toast.makeText(applicationContext,"app error", Toast.LENGTH_SHORT).show()
                            return@launch
                        }catch (e: HttpException){
                            Toast.makeText(applicationContext,"http error", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        //fetch forecast weather data
                        val responseForecast = try{
                            RetrofitInstance.api.getForecastWeatherData(
                                city = readWeatherData.name.toString(),
                                units = "metric",
                                latitude = readWeatherData.coord.lat.toDouble(),
                                longitude = readWeatherData.coord.lon.toDouble(),
                                cnt = 10
                            )
                        }catch (e : IOException){
                            Toast.makeText(applicationContext,"app error", Toast.LENGTH_SHORT).show()
                            return@launch
                        }catch (e: HttpException){
                            Toast.makeText(applicationContext,"http error", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        //Log.d("DEBUG","FETCH CURRENT: ${responseCurrent.isSuccessful}")
                        //Log.d("DEBUG","FETCH FORECAST: ${responseForecast.isSuccessful}")

                        if(responseCurrent.isSuccessful && responseCurrent.body() != null &&
                            responseForecast.isSuccessful && responseForecast.body() != null){

                            withContext(Dispatchers.Main){
                                //Log.d("DEBUG","FETCH $filenameLocation FROM API")

                                val cityCode = responseCurrent.body()!!.id
                                val filenameWeather = "${responseCurrent.body()!!.name.toString().lowercase()}${cityCode.toString()}"

                                //save fresh current data
                                JsonManager.saveJsonToInternalStorageCurrentData(
                                    context = applicationContext,
                                    dataWeather = responseCurrent.body()!!,
                                    filename = "weather_current_${filenameWeather}.json")

                                //save fresh forecast data
                                JsonManager.saveJsonToInternalStorageForecastData(
                                    context = applicationContext,
                                    dataWeather = responseForecast.body()!!,
                                    filename = "weather_forecast_${filenameWeather}.json")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun scheduleNextWork() {
        val workRequest = OneTimeWorkRequestBuilder<WeatherWorker>()
            .setInitialDelay(10, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "WeatherWorker",
            ExistingWorkPolicy.REPLACE,
            workRequest)
    }
}