package com.example.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class AddLocationDialog(exampleDataList: ArrayList<WeatherModel>) : DialogFragment() {

    private val itemList = exampleDataList
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.add_location_dialog, container, false)

        // cancel adding location
        rootView.findViewById<Button>(R.id.cancel_location_button).setOnClickListener{
            dismiss()
        }

        // add location to list
        rootView.findViewById<Button>(R.id.add_location_button).setOnClickListener{
            val insertLocation = rootView.findViewById<EditText>(R.id.location_input)
            // add item to list
            itemList.add(WeatherModel(insertLocation.text.toString(),"Pochmurnie"))
            dismiss()
        }

        return rootView
    }
}