package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.NetManager.Companion.checkAccessToNet
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbarMain))
        supportActionBar?.title = ""

        findViewById<FloatingActionButton>(R.id.addLocationBtn).setOnClickListener{
            Toast.makeText(this, "Location Add", Toast.LENGTH_SHORT).show()
        }

        // Make toast with net status info
        if (checkAccessToNet(this)) {
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

        // set weather adapte to recycleview
        recyclerViewWeather.adapter = WeatherAdapter(exampleDataList)
    }

    // Function to inflate menu bar options
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu,menu)

        val toogleBtn = menu.findItem(R.id.switch_metrics_btn).actionView as ToggleButton
        toogleBtn.text = "Metryki"

        toogleBtn.setOnCheckedChangeListener{_ , isChecked ->
            if (isChecked){
                toogleBtn.textOn = "metryka1"
                Toast.makeText(this,"Switch the metrics1",Toast.LENGTH_SHORT).show()
            }
            else{
                toogleBtn.textOff = "metryka2"
                Toast.makeText(this,"Switch the metrics2",Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }

    // Function to operate on items in toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Operate on option in toolbar
        return when (item.itemId) {
            R.id.refresh_data_btn -> {
                Log.d("CHANGE IN MENU","Refresh the data")
                Toast.makeText(this,"Refresh the data",Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}