package com.example.weatherapp

import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.NetManager.Companion.checkAccessToNet


class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbarMain))
        supportActionBar?.title = ""

        // Make toast with net status info
        if (checkAccessToNet(this)) {
            Toast.makeText(this, "Połączony z internetem", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to inflate menu bar options
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    // Function to operate on items in toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.switch_metrics_btn -> {

                Toast.makeText(this,"Switch the metrics",Toast.LENGTH_SHORT).show()
                true
            }
            R.id.refresh_data_btn -> {
                Toast.makeText(this,"Refresh the data",Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}