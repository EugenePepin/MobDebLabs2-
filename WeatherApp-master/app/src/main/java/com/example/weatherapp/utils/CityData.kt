package com.example.weatherapp.utils

data class CityData(
    val name: String,
    val isFavorite: Boolean = false,
    val isCurrentLocation: Boolean = false
) 