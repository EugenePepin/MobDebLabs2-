package com.example.weatherapp.utils

import com.example.weatherapp.R

object WeatherCondition {
    val rainConditions = listOf(
        "patchy rain possible",
        "patchy rain nearby",
        "moderate rain",
        "light drizzle",
        "heavy rain",
        "light rain",
        "rain",
        "thundery outbreaks possible",
        "moderate or heavy rain with thunder",
        "невелика злива",
        "можливі окремі дощі",
        "помірний дощ",
        "сильний дощ",
        "легкий дощ",
        "дощ",
        "мряка",
        "невеликий дощ зі снігом",
        "місцями дощ",
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
        "суцільна хмарність",
        "невелика хмарність",
        "частково хмарно",
        "хмарно",
        "похмуро",
        "туман"
    )

    private val conditionToBackground = buildMap {
        rainConditions.forEach { keyword ->
            put(keyword, R.drawable.background_rain)
        }
        snowConditions.forEach { keyword ->
            put(keyword, R.drawable.background_snow)
        }
        cloudCondition.forEach { keyword ->
            put(keyword, R.drawable.background_cloud)
        }
    }

    fun getBackgroundForCondition(condition: String): Int? {
        val normalized = condition.trim().replace("\\s+".toRegex(), " ")
        return conditionToBackground.entries.firstOrNull { (key, _) ->
            normalized.contains(key)
        }?.value
    }
}