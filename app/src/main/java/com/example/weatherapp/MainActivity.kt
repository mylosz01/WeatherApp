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
import com.example.weatherapp.netMenager.NetManager.Companion.checkAccessToInternet
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
        exampleDataList.add(WeatherModel("Kraków","Slonecznie"))
        exampleDataList.add(WeatherModel("Warszawa","Mglisto"))
        exampleDataList.add(WeatherModel("Łódź","Deszczowo"))
        exampleDataList.add(WeatherModel("Poznań","Pochmurnie"))
        exampleDataList.add(WeatherModel("Szczecin","Deszczowo"))
        exampleDataList.add(WeatherModel("Olsztyn","Slonecznie"))
        exampleDataList.add(WeatherModel("Zielona Góra","Pochmurnie"))
        exampleDataList.add(WeatherModel("Gdańsk","Mglisto"))
        exampleDataList.add(WeatherModel("Wrocław","Deszczowo"))

        // RecycleView for favorite location
        val recyclerViewWeather = findViewById<RecyclerView>(R.id.favorite_location_RV)
        recyclerViewWeather.layoutManager =  LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

        // set weather adapter to recycleview
        recyclerViewWeather.adapter = WeatherAdapter(exampleDataList,this)

        findViewById<FloatingActionButton>(R.id.addLocationBtn).setOnClickListener{
            Toast.makeText(this, "Location Add", Toast.LENGTH_SHORT).show()
            val dialog = AddLocationDialog(exampleDataList)
            dialog.show(supportFragmentManager,"Add location")
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
                RetrofitInstance.api.getCurrentWeatherData("Kraków","metric")
            }catch (e : IOException){
                Toast.makeText(applicationContext,"app error", Toast.LENGTH_SHORT).show()
                return@launch
            }catch (e: HttpException){
                Toast.makeText(applicationContext,"http error", Toast.LENGTH_SHORT).show()
                return@launch
            }

            if(response.isSuccessful && response.body() != null){
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