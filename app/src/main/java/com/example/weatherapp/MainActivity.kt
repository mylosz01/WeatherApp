package com.example.weatherapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.ToggleButton
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

        // example list of weather card array
        val exampleDataList = ArrayList<WeatherModel>()
        exampleDataList.add(WeatherModel(Utils.getWeatherImageResource(800),"Kraków","Slonecznie"))
        exampleDataList.add(WeatherModel(Utils.getWeatherImageResource(600),"Warszawa","Mglisto"))
        exampleDataList.add(WeatherModel(Utils.getWeatherImageResource(300),"Łódź","Deszczowo"))

        // RecycleView for favorite location
        val recyclerViewWeather = findViewById<RecyclerView>(R.id.favorite_location_RV)
        recyclerViewWeather.layoutManager =  LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        // set weather adapter to recycleview
        recyclerViewWeather.adapter = WeatherAdapter(exampleDataList,this)

        // floating button for adding new favourite location
        findViewById<FloatingActionButton>(R.id.addLocationBtn).setOnClickListener{

            //check if internet connection available
            if (!checkAccessToInternet(this)) {
                Toast.makeText(this, "No internet connection to add location", Toast.LENGTH_LONG).show()
            } else {
                val dialog = AddLocationDialog(exampleDataList)
                dialog.show(supportFragmentManager,"Add location")
            }
        }

        // fetch weather data from api
        getCurrentWeather()
    }

    //function to start weather activity by click on item
    override fun clickedItem(weatherModel: WeatherModel){
        var weatherIntent = Intent(this,WeatherActivity::class.java)
        weatherIntent.putExtra("Location",weatherModel.getLocationName())
        startActivity(weatherIntent)
    }

    //fetch weather data from api
    private fun getCurrentWeather() {
        GlobalScope.launch(Dispatchers.IO) {
            val response = try{
                RetrofitInstance.api.getCurrentWeatherData(city = "Kraków", units = "metric")
            }catch (e : IOException){
                Toast.makeText(applicationContext,"app error", Toast.LENGTH_SHORT).show()
                return@launch
            }catch (e: HttpException){
                Toast.makeText(applicationContext,"http error", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if(response.isSuccessful && response.body() != null){
                val weatherData = response.body()!!
                JsonManager.saveJsonToInternalStorage(applicationContext,weatherData,"weather_data.json")

                //JsonManager.deleteFileFromInternalStorage(applicationContext,"weather_data.json")

                val readWeatherData : CurrentWeatherResponseApi? = JsonManager.readJsonFromInternalStorage(applicationContext,"weather_data.json")

                Log.d("DEBUG","Read data weather ${readWeatherData!!.main?.temp}")

                withContext(Dispatchers.Main){
                    Log.d("DEBUG","Temperature: ${response.body()!!.main?.temp}")
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
                Toast.makeText(this,"Refresh the data",Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}