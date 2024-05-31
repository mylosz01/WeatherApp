package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Utils.RetrofitInstance
import com.example.weatherapp.Utils.Utils
import com.example.weatherapp.jsonManager.JsonManager
import com.example.weatherapp.weatherMainRV.WeatherAdapter
import com.example.weatherapp.weatherMainRV.WeatherModel
import com.example.weatherapp.netMenager.NetManager.Companion.checkAccessToInternet
import com.example.weatherapp.weatherResponseData.CurrentWeatherResponseApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity(), WeatherAdapter.ClickListener {

    private val weatherViewModel : WeatherViewModel by viewModels<WeatherViewModel>()
    private lateinit var weatherAdapter: WeatherAdapter

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
        recyclerViewWeather.adapter = WeatherAdapter(weatherViewModel.weatherLocationArray,this)
        weatherAdapter = recyclerViewWeather.adapter as WeatherAdapter

        // floating button for adding new favourite location
        findViewById<FloatingActionButton>(R.id.addLocationBtn).setOnClickListener{

            //check if internet connection available
            if (!checkAccessToInternet(this)) {
                Toast.makeText(this, "No internet connection to add location", Toast.LENGTH_LONG).show()
            } else {
                val dialog = AddLocationDialog(weatherViewModel.weatherLocationArray)
                dialog.attachContext(applicationContext)
                dialog.show(supportFragmentManager,"Add location")
            }
        }

        // fetch current weather data from api
        if(checkAccessToInternet(this)){
            fetchCurrentWeatherData()
            weatherAdapter.notifyDataSetChanged()
        }
        else{
            readFromStorageCurrentWeather()
            weatherAdapter.notifyDataSetChanged()
        }
    }

    //function to start weather activity by click on item
    override fun clickedItem(weatherModel: WeatherModel){
        val weatherIntent = Intent(this,WeatherActivity::class.java).apply {
            Log.d("DEBUG", " FILE NAME : ${weatherModel.getFilename()} ")
            putExtra("CurrentWeatherData",JsonManager.readJsonFromInternalStorage(applicationContext,weatherModel.getFilename())!!)
        }
        startActivity(weatherIntent)
    }

    private fun getFileCurrentDataList(): List<String> {
        val fileList = applicationContext.filesDir.listFiles { _, name ->
            name.startsWith("weather_current_") && name.endsWith(".json")
        }
        return fileList?.map { it.name } ?: emptyList()
    }

    //fetch current weather data from api for location
    private fun fetchCurrentWeatherData() {

        val fileListCurrentData = getFileCurrentDataList()
        // get file with current data weather
        Log.d("DEBUG","${fileListCurrentData.toMutableList()}")

        // if list is not empty fetch data from weather api
        if(fileListCurrentData.isNotEmpty()){

            weatherViewModel.weatherLocationArray.clear()

            for(filenameLocation: String in fileListCurrentData){

                val readWeatherData : CurrentWeatherResponseApi? = JsonManager.readJsonFromInternalStorage(
                    applicationContext,filenameLocation)

                // delete old data
                JsonManager.deleteFileFromInternalStorage(
                    context = applicationContext,
                    filename = filenameLocation)

                if(readWeatherData != null){

                    GlobalScope.launch(Dispatchers.IO) {
                        val response = try{
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

                        if(response.isSuccessful && response.body() != null){
                            withContext(Dispatchers.Main){
                                Log.d("DEBUG","FETCH $filenameLocation FROM API")

                                val cityCode = response.body()!!.id
                                val filenameWeather = "${response.body()!!.name.toString().lowercase()}${cityCode.toString()}"

                                val weatherModelNew = WeatherModel(
                                    imageWeatherId = Utils.getWeatherImageResource(response.body()!!.weather?.get(0)?.id!!.toInt()),
                                    locationName = response.body()!!.name.toString(),
                                    descriptionInfo = response.body()!!.weather?.get(0)?.description.toString(),
                                    humidityPercent = response.body()!!.main?.humidity!!.toDouble(),
                                    temperature = response.body()!!.main?.temp!!.toDouble(),
                                    windSpeed = response.body()!!.wind?.speed!!.toDouble(),
                                    filenameCurrentWeather = "weather_current_${filenameWeather}.json"
                                )

                                // load weatherModel to list
                                weatherViewModel.weatherLocationArray.add(weatherModelNew)
                                weatherAdapter.notifyDataSetChanged()

                                //save fresh data
                                JsonManager.saveJsonToInternalStorage(
                                    context = applicationContext,
                                    dataWeather = readWeatherData,
                                    filename = weatherModelNew.getFilename())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun readFromStorageCurrentWeather(){
        val fileListCurrentData = getFileCurrentDataList()
        // get file with current data weather
        Log.d("DEBUG","${fileListCurrentData.toMutableList()}")

        // if list is not empty read from storage data
        if(fileListCurrentData.isNotEmpty()){
            for(filenameLocation: String in fileListCurrentData){

                val readWeatherData : CurrentWeatherResponseApi? = JsonManager.readJsonFromInternalStorage(
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
                        filenameCurrentWeather = filenameLocation
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

        val toogleBtn = menu.findItem(R.id.switch_metrics_btn).actionView as ToggleButton
        toogleBtn.text = "Units"

        toogleBtn.setOnCheckedChangeListener{_ , isChecked ->
            if (isChecked){
                toogleBtn.textOn = "Metric"
                Toast.makeText(this,"Switch the metrics1",Toast.LENGTH_SHORT).show()
                Log.d("DEBUG","Use metric units")
            }
            else{
                toogleBtn.textOff = "Imperial"
                Toast.makeText(this,"Switch the metrics2",Toast.LENGTH_SHORT).show()
                Log.d("DEBUG","Use imperial units")
            }
        }

        return true
    }

    // Function to operate on items in toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Operate on option in toolbar
        return when (item.itemId) {
            R.id.refresh_data_btn -> {
                Log.d("DEBUG","Refresh the data")
                if(checkAccessToInternet(applicationContext)){
                    fetchCurrentWeatherData()
                    weatherAdapter.notifyDataSetChanged()
                    Toast.makeText(this,"Refresh the data",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"Can't refresh data due no connection",Toast.LENGTH_SHORT).show()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}