package com.example.weatherapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.NetManager.Companion.checkAccessToNet


class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Make toast with net status info
        if (checkAccessToNet(this)) {
            Toast.makeText(this, "Połączony z internetem", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show()
        }
    }
}