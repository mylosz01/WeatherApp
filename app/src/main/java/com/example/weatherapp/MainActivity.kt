package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapp.Utils.RetrofitInstance
import com.example.weatherapp.Utils.Utils
import com.example.weatherapp.jsonManager.JsonManager
import com.example.weatherapp.netMenager.NetManager.Companion.checkAccessToInternet
import com.example.weatherapp.weatherMainRV.WeatherAdapter
import com.example.weatherapp.weatherMainRV.WeatherModel
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), WeatherAdapter.ClickListener//, WeatherAdapter.DeleteListener
{

    private val weatherViewModel : WeatherViewModel by viewModels<WeatherViewModel>()
    private lateinit var weatherAdapter: WeatherAdapter

    companion object{
        const val PREFS_NAME = "com.example.weatherapp"
        private const val WORKER_STARTED = "worker_started"
        const val USER_UNITS = "metric"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbarMain))
        supportActionBar?.title = ""


        // Make toast with net status info
        if (checkAccessToInternet(this)) {
            Toast.makeText(this, "Internet connection !!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }

        // RecycleView for favorite location
        val recyclerViewWeather = findViewById<RecyclerView>(R.id.favorite_location_RV)
        recyclerViewWeather.layoutManager =  LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        // set weather adapter to recycleview
        weatherAdapter = WeatherAdapter(weatherViewModel,this)
        recyclerViewWeather.adapter = weatherAdapter


        // floating button for adding new favourite location
        findViewById<FloatingActionButton>(R.id.addLocationBtn).setOnClickListener{

            //check if internet connection available
            if (!checkAccessToInternet(this)) {
                Toast.makeText(this, "No internet connection to add location", Toast.LENGTH_LONG).show()
            } else {
                val dialog = AddLocationDialog(weatherAdapter,weatherViewModel.weatherLocationArray)
                dialog.attachContext(applicationContext)
                dialog.show(supportFragmentManager,"Add location")
            }
        }


        // fetch current weather data from api
        if(checkAccessToInternet(this)){
            fetchCurrentWeatherData()
            weatherAdapter.notifyItemRangeInserted(0,weatherViewModel.weatherLocationArray.size - 1)
        }
        else{
            // read stored weather data
            readFromStorageCurrentWeather()
            weatherAdapter.notifyItemRangeInserted(0,weatherViewModel.weatherLocationArray.size - 1)
        }


        // check by shared preferences if schedule worker started
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val workedStarted = sharedPreferences.getBoolean(WORKER_STARTED,false)

        Log.d("DEBUG","Worker status: $workedStarted")

        if(!workedStarted) {
            Log.d("DEBUG","START SCHEDULE WORKER")
            scheduleWeatherUpdates(sharedPreferences)
        }
    }


    //schedule update weather
    private fun scheduleWeatherUpdates(sharedPreferences: SharedPreferences){
        val workRequest = OneTimeWorkRequestBuilder<WeatherWorker>()
            .setInitialDelay(10,TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "WeatherWorker",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        sharedPreferences.edit().putBoolean(WORKER_STARTED,true).apply()
    }

    //check file in array
    private fun checkFileLocationArray(weatherModelArray : ArrayList<WeatherModel>, checkWeatherFilename : String): Boolean {

        for(weatherEle in weatherModelArray){
            if(weatherEle.getFilenameCurrentWeather() == checkWeatherFilename)
                return true
        }
        return  false
    }

    private fun checkFetchDataTime(dt: Int?): Boolean{
        val currentTimeInMillis = System.currentTimeMillis()
        val tenMinutesInMillis = 10 * 60 * 1000

        val tenMinutesAgo = currentTimeInMillis - tenMinutesInMillis

        return dt!!.toLong() < tenMinutesAgo
    }

    //function to start weather activity by click on item
    override fun clickedItem(weatherModel: WeatherModel){

        try {
            val weatherIntent = Intent(this, WeatherActivity::class.java).apply {
                Log.d("DEBUG", " FILE NAME : ${weatherModel.getFilenameCurrentWeather()} ")
                putExtra(
                    "CurrentWeatherData",
                    JsonManager.readJsonFromInternalStorageCurrentData(
                        applicationContext,
                        weatherModel.getFilenameCurrentWeather()
                    )!!
                )
                putExtra(
                    "ForecastWeatherData",
                    JsonManager.readJsonFromInternalStorageForecastData(
                        applicationContext,
                        weatherModel.getFilenameForecastWeather()
                    )!!
                )
            }
            startActivity(weatherIntent)
        }
        catch (e: NullPointerException){
            Log.d("DEBUG","Some error to start intent")
        }
    }

    // return list of files with current weather data
    private fun getFileCurrentDataList(): List<String> {
        val fileList = applicationContext.filesDir.listFiles { _, name ->
            name.startsWith("weather_current_") && name.endsWith(".json")
        }
        return fileList?.map { it.name } ?: emptyList()
    }

    //fetch current weather data from api for location
    private fun fetchCurrentWeatherData() {

        // get file with current data weather
        val fileListCurrentData = getFileCurrentDataList()
        Log.d("DEBUG","${fileListCurrentData.toMutableList()}")

        // if list is not empty fetch data from weather api
        if(fileListCurrentData.isNotEmpty()){

            weatherViewModel.weatherLocationArray.clear()

            for(filenameLocation: String in fileListCurrentData){

                val readWeatherData : CurrentWeatherResponseApi? = JsonManager.readJsonFromInternalStorageCurrentData(
                    applicationContext,filenameLocation)

                //Log.d("DEBUG", "TIME OF FETCH: ${checkFetchDataTime(readWeatherData!!.dt)}")

                // if file currently in array and time of fetch is less than ... skip
                if(checkFileLocationArray(weatherViewModel.weatherLocationArray,filenameLocation) && checkFetchDataTime(readWeatherData!!.dt))
                    continue

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

                        Log.d("DEBUG","FETCH CURRENT: ${responseCurrent.isSuccessful}")
                        Log.d("DEBUG","FETCH FORECAST: ${responseForecast.isSuccessful}")

                        if(responseCurrent.isSuccessful && responseCurrent.body() != null &&
                            responseForecast.isSuccessful && responseForecast.body() != null){

                            withContext(Dispatchers.Main){
                                Log.d("DEBUG","FETCH $filenameLocation FROM API")

                                val cityCode = responseCurrent.body()!!.id
                                val filenameWeather = "${responseCurrent.body()!!.name.toString().lowercase()}${cityCode.toString()}"

                                // create object WeatherModel to store info
                                val weatherModelNew = WeatherModel(
                                    imageWeatherId = Utils.getWeatherImageResource(responseCurrent.body()!!.weather?.get(0)?.id!!.toInt()),
                                    locationName = responseCurrent.body()!!.name.toString(),
                                    descriptionInfo = responseCurrent.body()!!.weather?.get(0)?.description.toString(),
                                    humidityPercent = responseCurrent.body()!!.main?.humidity!!.toDouble(),
                                    temperature = responseCurrent.body()!!.main?.temp!!.toDouble(),
                                    windSpeed = responseCurrent.body()!!.wind?.speed!!.toDouble(),
                                    filenameCurrentWeather = "weather_current_${filenameWeather}.json",
                                    filenameForecastWeather = "weather_forecast_${filenameWeather}.json"
                                )

                                // load weatherModel to list
                                weatherViewModel.weatherLocationArray.add(weatherModelNew)
                                weatherAdapter.notifyDataSetChanged()

                                //save fresh current data
                                JsonManager.saveJsonToInternalStorageCurrentData(
                                    context = applicationContext,
                                    dataWeather = responseCurrent.body()!!,
                                    filename = weatherModelNew.getFilenameCurrentWeather())

                                //save fresh forecast data
                                JsonManager.saveJsonToInternalStorageForecastData(
                                    context = applicationContext,
                                    dataWeather = responseForecast.body()!!,
                                    filename = weatherModelNew.getFilenameForecastWeather())
                            }
                        }
                    }
                }
            }
        }
    }

    // function to read weather data from json
    private fun readFromStorageCurrentWeather(){
        val fileListCurrentData = getFileCurrentDataList()
        // get file with current data weather
        Log.d("DEBUG","${fileListCurrentData.toMutableList()}")

        // if list is not empty read from storage data
        if(fileListCurrentData.isNotEmpty()){

            for(filenameLocation: String in fileListCurrentData){

                // if file currently in array skip
                if(checkFileLocationArray(weatherViewModel.weatherLocationArray,filenameLocation))
                    continue

                val readWeatherData : CurrentWeatherResponseApi? = JsonManager.readJsonFromInternalStorageCurrentData(
                    applicationContext,filenameLocation)

                Log.d("DEBUG","READ $filenameLocation FROM STORAGE")

                if(readWeatherData != null){

                    val weatherModelNew = WeatherModel(
                        imageWeatherId = Utils.getWeatherImageResource(readWeatherData.weather?.get(0)?.id!!.toInt()),
                        locationName = readWeatherData.name.toString(),
                        descriptionInfo = readWeatherData.weather[0]?.description.toString(),
                        humidityPercent = readWeatherData.main?.humidity!!.toDouble(),
                        temperature = readWeatherData.main.temp!!.toDouble(),
                        windSpeed = readWeatherData.wind?.speed!!.toDouble(),
                        filenameCurrentWeather = filenameLocation,
                        filenameForecastWeather = filenameLocation.replace("current","forecast")
                    )

                    // load weatherModel to list
                    weatherViewModel.weatherLocationArray.add(weatherModelNew)
                    weatherAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    // Function to inflate menu bar options
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu,menu)

        val switchMetricsBtn = menu.findItem(R.id.switch_metrics_btn)
        val sharedPreferences = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        val userUnits = sharedPreferences.getString(USER_UNITS, "metric")
        switchMetricsBtn?.title = userUnits
        return true
    }

    // Function to operate on items in toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Operate on option in toolbar
        return when (item.itemId) {
            // select refresh btn
            R.id.refresh_data_btn -> {
                //Log.d("DEBUG","Refresh the data")
                if(checkAccessToInternet(applicationContext)){
                    fetchCurrentWeatherData()
                    weatherAdapter.notifyItemRangeChanged(0,weatherViewModel.weatherLocationArray.size - 1)
                    Toast.makeText(this,"Refresh the data",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"Can't refresh data due no connection",Toast.LENGTH_SHORT).show()
                }
                true
            }
            // select switch metrics btn
            R.id.switch_metrics_btn -> {
                showPopupMenu(findViewById(R.id.switch_metrics_btn))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showPopupMenu(view: View){
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)

        val switchBtn = findViewById<ActionMenuItemView>(R.id.switch_metrics_btn)
        val sharedPreferences = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.switch_unit_standard -> {
                    switchBtn.text = getString(R.string.standard)
                    sharedPreferences.edit().putString(USER_UNITS,"standard").apply()
                    Toast.makeText(this, "Standard units", Toast.LENGTH_SHORT).show()
                }
                R.id.switch_unit_metric -> {
                    switchBtn.text = getString(R.string.metric)
                    sharedPreferences.edit().putString(USER_UNITS,"metric").apply()
                    Toast.makeText(this, "Metric units", Toast.LENGTH_SHORT).show()
                }
                R.id.switch_unit_imperial -> {
                    switchBtn.text = getString(R.string.imperial)
                    sharedPreferences.edit().putString(USER_UNITS,"imperial").apply()
                    Toast.makeText(this, "Imperial units", Toast.LENGTH_SHORT).show()
                }
            }
            fetchCurrentWeatherData()
            weatherAdapter.notifyItemRangeChanged(0,weatherViewModel.weatherLocationArray.size - 1)
            true
        }
        popupMenu.show()
    }
}