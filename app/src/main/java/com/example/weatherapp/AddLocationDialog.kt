package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.Utils.RetrofitInstance
import com.example.weatherapp.Utils.Utils
import com.example.weatherapp.jsonManager.JsonManager
import com.example.weatherapp.weatherMainRV.WeatherModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class AddLocationDialog(weatherDataArrayList: ArrayList<WeatherModel>) : DialogFragment() {

    private val itemList = weatherDataArrayList
    private lateinit var adapterAC: ArrayAdapter<String>
    private lateinit var insertLocationACTV: AutoCompleteTextView
    private lateinit var context: Context

    constructor() : this(ArrayList<WeatherModel>())

    override fun onDestroyView() {
        super.onDestroyView()
        adapterAC.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.add_location_dialog, container, false)

        // get AutoCompleteTextView
        insertLocationACTV = rootView.findViewById(R.id.location_input)

        // initialize adapter
        adapterAC = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listOf())
        insertLocationACTV.setAdapter(adapterAC)

        // set listener to fetch suggestion
        /*insertLocationACTV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Log.d("DEBUG","On Text Changed Search Location")
                val query = s.toString()
                if (query.isNotEmpty()) {
                    fetchSuggestions(query)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })*/

        // cancel adding location
        rootView.findViewById<Button>(R.id.cancel_location_button).setOnClickListener{
            dismiss()
        }

        // add location to list
        rootView.findViewById<Button>(R.id.add_location_button).setOnClickListener{
            val fetchData = insertLocationACTV.text.split(*arrayOf(",","/"))
            Log.d("DEBUG","Location name : $fetchData")

            if(fetchData[0].isNotEmpty()) {

                val cityName = fetchData[0]
                var latitude = 0.0
                var longitude = 0.0

                if(fetchData.size >= 3) {
                    latitude = fetchData[2].toDoubleOrNull() ?: 0.0
                    longitude = fetchData[3].toDoubleOrNull() ?: 0.0
                }

                // fetching data from api and add new item to list
                fetchCurrentWeather(cityName = cityName, units = "metric", latitude = latitude, longitude =  longitude, this.context)
            }

            adapterAC.notifyDataSetChanged()
            dismiss()
        }

        return rootView
    }

    // function to fetch current weather data
    private fun fetchCurrentWeather(cityName: String, units: String = "metric", latitude: Double = 0.0, longitude: Double = 0.0, context: Context){
        GlobalScope.launch(Dispatchers.IO) {
            val response = try{
                RetrofitInstance.api.getCurrentWeatherData(city = cityName, units = units, latitude = latitude, longitude = longitude)
            }catch (e : IOException){
                Toast.makeText(requireContext(),"app error", Toast.LENGTH_SHORT).show()
                return@launch
            }catch (e: HttpException){
                Toast.makeText(requireContext(),"http error", Toast.LENGTH_SHORT).show()
                return@launch
            }

            Log.d("DEBUG","CHECK RESPONSE: ${response.isSuccessful}")

            if(response.isSuccessful && response.body() != null){
                withContext(Dispatchers.Main){
                    
                    try{
                        val cityCode = response.body()!!.id
                        val filenameWeather = cityName.lowercase() + cityCode.toString()

                        // create new WeatherModel object and extract weather data
                        val weatherModelNew = WeatherModel(
                            imageWeatherId = Utils.getWeatherImageResource(response.body()!!.weather?.get(0)?.id!!.toInt()),
                            locationName = cityName,
                            descriptionInfo = response.body()!!.weather?.get(0)?.description.toString(),
                            humidityPercent = response.body()!!.main?.humidity!!.toDouble(),
                            temperature = response.body()!!.main?.temp!!.toDouble(),
                            windSpeed = response.body()!!.wind?.speed!!.toDouble(),
                            filenameLocation = filenameWeather
                        )
                        // add to list
                        itemList.add(weatherModelNew)
                        Log.d("DEBUG","CITY $cityName added")
                        adapterAC.notifyDataSetChanged()

                        //save current data weather
                        JsonManager.saveJsonToInternalStorage(context,response.body()!!,"weather_current_${filenameWeather}.json")

                    }
                    catch (_: Exception){
                        return@withContext
                    }
                }
            }
        }
    }

    private fun fetchSuggestions(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.api.getCitySuggestions(query)
            if (response.isSuccessful && response.body() != null) {
                val suggestions = response.body()!!.map { "${it.name}, ${it.country}, ${String.format("%.2f",it.lat).toDouble()} / ${String.format("%.2f",it.lon).toDouble()}" }
                withContext(Dispatchers.Main) {
                    adapterAC.clear()
                    adapterAC.addAll(suggestions)
                    adapterAC.notifyDataSetChanged()
                }
            }
        }
    }

    fun attachContext(requireContext: Context = requireContext()) {
        this.context = requireContext
    }
}