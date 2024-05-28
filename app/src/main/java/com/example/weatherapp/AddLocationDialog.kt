package com.example.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.weatherapp.weatherMainRV.WeatherModel

class AddLocationDialog(exampleDataList: ArrayList<WeatherModel>) : DialogFragment() {

    private val itemList = exampleDataList
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.add_location_dialog, container, false)

        val cities = listOf("Kraków", "Warszawa", "Gdańsk", "Poznań", "Wrocław", "Łódź", "Szczecin", "Bydgoszcz")


        // get AutoCompleteTextView
        val insertLocationACTV: AutoCompleteTextView = rootView.findViewById(R.id.location_input)

        // set adapter for AutoCompleteTextView
        val adapterAC = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
        insertLocationACTV.setAdapter(adapterAC)

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
}