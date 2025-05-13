package com.example.weatherapp.utils

import com.example.weatherapp.R

object WeatherCondition {
    val rainConditions = listOf(
        "patchy rain possible",
        "moderate rain",
        "heavy rain",
        "light rain",
        "rain",
        "thundery outbreaks possible",
        "moderate or heavy rain with thunder",
        "можливі окремі дощі",
        "помірний дощ",
        "сильний дощ",
        "легкий дощ",
        "дощ",
        "можливі грозові пориви",
        "помірний або сильний дощ з грозою"
    )

    val snowConditions = listOf(
        "patchy snow possible",
        "blowing snow",
        "blizzard",
        "patchy light snow",
        "light snow",
        "patchy moderate snow",
        "moderate snow",
        "patchy heavy snow",
        "heavy snow",
        "light snow showers",
        "moderate or heavy snow showers",
        "patchy light snow with thunder",
        "moderate or heavy snow with thunder",
        "можливий місцями сніг",
        "хуртовина",
        "завірюха",
        "місцями легкий сніг",
        "легкий сніг",
        "місцями помірний сніг",
        "помірний сніг",
        "місцями сильний сніг",
        "сильний сніг",
        "легкі снігопади",
        "помірні або сильні снігопади",
        "місцями легкий сніг з грозою",
        "помірний або сильний сніг з грозою"
    )

    val cloudCondition = listOf(
        "Partly cloudy",
        "cloudy",
        "overcast",
        "fog",
        "невелика хмарність",
        "частково хмарно",
        "хмарно",
        "похмуро",
        "туман"
    )

    private val conditionToBackground = buildMap {
        rainConditions.forEach { keyword ->
            put(keyword, R.drawable.rain_background)
        }
        snowConditions.forEach { keyword ->
            put(keyword, R.drawable.snow_background)
        }
        cloudCondition.forEach { keyword ->
            put(keyword, R.drawable.cloud_background)
        }
    }

    fun getBackgroundForCondition(condition: String): Int? {
        val normalized = condition.trim().lowercase().replace("\\s+".toRegex(), " ")
        return conditionToBackground.entries.firstOrNull { (key, _) ->
            normalized.contains(key)
        }?.value
    }
}