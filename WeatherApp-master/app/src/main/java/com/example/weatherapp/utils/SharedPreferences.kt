package com.example.weatherapp.utils

import android.content.Context
import java.util.Locale


object SharedPreferences {
    private const val PREF_NAME = "weather_prefs"
    private const val KEY_TEMP_UNIT_CELSIUS = "temp_unit_celsius"
    private const val KEY_LANGUAGE = "language"

    fun saveTemperatureUnit(context: Context, isCelsius: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_TEMP_UNIT_CELSIUS, isCelsius).apply()
    }

    fun getTemperatureUnit(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_TEMP_UNIT_CELSIUS, true)
    }

    fun saveLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }
}

