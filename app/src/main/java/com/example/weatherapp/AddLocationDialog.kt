package com.example.weatherapp

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
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.Utils.RetrofitInstance
import com.example.weatherapp.weatherMainRV.WeatherModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.round

class AddLocationDialog(exampleDataList: ArrayList<WeatherModel>) : DialogFragment() {

    private val itemList = exampleDataList
    private lateinit var adapterAC: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.add_location_dialog, container, false)

        // get AutoCompleteTextView
        val insertLocationACTV: AutoCompleteTextView = rootView.findViewById(R.id.location_input)

        // initialize adapter
        adapterAC = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listOf())
        insertLocationACTV.setAdapter(adapterAC)

        // set listener to fetch suggestion
        insertLocationACTV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("DEBUG","On Text Changed Search Location")
                val query = s.toString()
                if (query.isNotEmpty()) {
                    fetchSuggestions(query)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // cancel adding location
        rootView.findViewById<Button>(R.id.cancel_location_button).setOnClickListener{
            dismiss()
        }

        // add location to list
        rootView.findViewById<Button>(R.id.add_location_button).setOnClickListener{
            // add item to list
            itemList.add(WeatherModel(insertLocationACTV.text.toString(),"Pochmurnie"))
            dismiss()
        }

        return rootView
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
}