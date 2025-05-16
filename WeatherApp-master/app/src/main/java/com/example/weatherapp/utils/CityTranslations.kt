package com.example.weatherapp.utils

object CityTranslations {
    private val cityTranslations = mapOf(
        "Київ" to "Kyiv",
        "Харків" to "Kharkiv",
        "Одеса" to "Odesa",
        "Дніпро" to "Dnipro",
        "Донецьк" to "Donetsk",
        "Запоріжжя" to "Zaporizhzhia",
        "Львів" to "Lviv",
        "Кривий Ріг" to "Kryvyi Rih",
        "Миколаїв" to "Mykolaiv",
        "Маріуполь" to "Mariupol",
        "Вінниця" to "Vinnytsia",
        "Херсон" to "Kherson",
        "Полтава" to "Poltava",
        "Черкаси" to "Cherkasy",
        "Суми" to "Sumy",
        "Хмельницький" to "Khmelnytskyi",
        "Чернівці" to "Chernivtsi",
        "Житомир" to "Zhytomyr",
        "Рівне" to "Rivne",
        "Кропивницький" to "Kropyvnytskyi",
        "Івано-Франківськ" to "Ivano-Frankivsk",
        "Тернопіль" to "Ternopil",
        "Луцьк" to "Lutsk",
        "Ужгород" to "Uzhhorod",
        "Чернігів" to "Chernihiv",
        "Севастополь" to "Sevastopol",
        "Сімферополь" to "Simferopol",
        "Мелитополь" to "Melitopol",
        "Кременчук" to "Kremenchuk",
        "Біла Церква" to "Bila Tserkva",
        "Кам'янець-Подільський" to "Kamianets-Podilskyi",
        "Бердянськ" to "Berdiansk",
        "Нікополь" to "Nikopol",
        "Слов'янськ" to "Sloviansk",
        "Алчевськ" to "Alchevsk",
        "Павлоград" to "Pavlohrad",
        "Умань" to "Uman",
        "Бровари" to "Brovary",
        "Мукачеве" to "Mukachevo",
        "Ялта" to "Yalta"
    )

    fun translateToEnglish(ukrainianName: String): String {
        cityTranslations[ukrainianName]?.let { return it }

        val matchingCities = cityTranslations.entries.filter { (ukr, _) ->
            ukr.startsWith(ukrainianName, ignoreCase = true)
        }
        
        return if (matchingCities.isNotEmpty()) {
            matchingCities.first().value
        } else {
            ukrainianName
        }
    }

    fun translateToUkrainian(englishName: String): String {
        cityTranslations.entries.find { it.value == englishName }?.let { return it.key }

        val matchingCities = cityTranslations.entries.filter { (_, eng) ->
            eng.startsWith(englishName, ignoreCase = true)
        }
        
        return if (matchingCities.isNotEmpty()) {
            matchingCities.first().key
        } else {
            englishName
        }
    }
} 