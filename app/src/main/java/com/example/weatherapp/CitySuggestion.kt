package com.example.weatherapp

data class CitySuggestion(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String
)
