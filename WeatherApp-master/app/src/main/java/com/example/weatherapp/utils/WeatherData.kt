package com.example.weatherapp.utils

data class WeatherData(

    val cityNameData: String,
    val dateAndTimeData: String,
    val conditionStatusData: String,
    val currentTempDataCelsius: String,
    val currentTempDataFahrenheit: String,
    val iconUrl: String,
    val maxTempDataCelsius: String,
    val minTempDataCelsius: String,
    val maxTempDataFahrenheit: String,
    val minTempDataFahrenheit: String,
    var hoursData: String

)
