package com.example.weatherapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.adapter.CityAdapter
import com.example.weatherapp.databinding.ActivityCitySearchBinding
import com.example.weatherapp.utils.CityData
import com.example.weatherapp.utils.CityTranslations
import com.example.weatherapp.utils.SharedPreferences
import org.json.JSONArray

class CitySearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCitySearchBinding
    private lateinit var cityAdapter: CityAdapter
    private val favoriteCities = mutableListOf<CityData>()
    private var currentLocationCity: CityData? = null

    companion object {
        private const val PREFS_NAME = "location_prefs"
        private const val KEY_LAST_CITY = "last_city"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupRecyclerView()
        setupSearchView()
        setupKeyboardDismissal()
        loadFavoriteCities()
        loadLastLocation()
    }

    override fun onResume() {
        super.onResume()
        updateCitiesList()
    }
    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(
                ContextCompat.getDrawable(
                    this@CitySearchActivity,
                    R.drawable.settings_dark_arrow
                )
            )
        }
    }

    private fun setupRecyclerView() {
        cityAdapter = CityAdapter(
            favoriteCities,
            onCityClick = { city ->
                hideKeyboard()
                val cityForApi = if (city.name.any { it.code in 0x0400..0x04FF }) {
                    CityTranslations.translateToEnglish(city.name)
                } else {
                    city.name
                }
                setResult(RESULT_OK, intent.putExtra("city", cityForApi))
                finish()
            },
            onDeleteClick = { city ->
                hideKeyboard()
                if (!city.isCurrentLocation) {
                    removeCity(city)
                }
            }
        )
        binding.citiesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CitySearchActivity)
            adapter = cityAdapter
            addOnItemTouchListener(object :
                androidx.recyclerview.widget.RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(
                    rv: androidx.recyclerview.widget.RecyclerView,
                    e: android.view.MotionEvent
                ): Boolean {
                    hideKeyboard()
                    return false
                }

                override fun onTouchEvent(
                    rv: androidx.recyclerview.widget.RecyclerView,
                    e: android.view.MotionEvent
                ) {
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }
    }

    private fun setupSearchView() {
        binding.searchView.apply {
            setOnItemClickListener { _, _, position, _ ->
                handleCitySelection(adapter.getItem(position) as String)
            }

            addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    val query = s?.toString() ?: ""
                    if (query.length >= 2) {
                        searchCity(query)
                    } else {
                        dismissDropDown()
                    }
                }
            })
        }
    }

    private fun handleCitySelection(selectedCity: String) {

        if (!favoriteCities.any { it.name == selectedCity }) {
            addCityToFavorites(CityData(selectedCity, false))
        } else {
            Toast.makeText(this, R.string.toast_message_city_already_exists, Toast.LENGTH_SHORT)
                .show()
        }

        binding.searchView.dismissDropDown()
        binding.searchView.setText("")
        hideKeyboard()
    }

    private fun searchCity(query: String) {
        val lang = SharedPreferences.getLanguage(this)
        val trimmedQuery = query.trim()
        if (trimmedQuery.length < 2) return
        
        val isUkrainianQuery = trimmedQuery.any { it.code in 0x0400..0x04FF }
        val translatedQuery =
            if (isUkrainianQuery) CityTranslations.translateToEnglish(trimmedQuery) else trimmedQuery
        val encodedQuery = java.net.URLEncoder.encode(translatedQuery, "UTF-8")
        val url =
            "https://api.weatherapi.com/v1/search.json?key=${BuildConfig.WEATHER_API_KEY}&q=$encodedQuery&lang=$lang"

        Volley.newRequestQueue(this).add(StringRequest(Request.Method.GET, url,
            { response -> handleSearchResponse(response, isUkrainianQuery) },
            { error -> error.printStackTrace() }
        ))
    }

    private fun handleSearchResponse(response: String, isUkrainianQuery: Boolean) {
        try {
            val utf8Response = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
            val cities = JSONArray(utf8Response).let { jsonArray ->
                val citySet = mutableSetOf<String>()
                val results = mutableListOf<String>()

                if (isUkrainianQuery) {
                    for (i in 0 until jsonArray.length()) {
                        val cityObj = jsonArray.getJSONObject(i)
                        if (cityObj.getString("country") == "Ukraine") {
                            val ukrainianName =
                                CityTranslations.translateToUkrainian(cityObj.getString("name"))
                            if (ukrainianName !in citySet) {
                                citySet.add(ukrainianName)
                                results.add(ukrainianName)
                            }
                        }
                    }
                }

                for (i in 0 until jsonArray.length()) {
                    val cityObj = jsonArray.getJSONObject(i)
                    if (!isUkrainianQuery || cityObj.getString("country") != "Ukraine") {
                        val cityName = cityObj.getString("name")
                        if (cityName !in citySet) {
                            citySet.add(cityName)
                            results.add(cityName)
                        }
                    }
                }

                results
            }
            binding.searchView.setAdapter(ArrayAdapter(this, R.layout.list_item_search_dropdown, cities))
            if (!binding.searchView.text.isNullOrBlank()) {
                binding.searchView.showDropDown()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadLastLocation() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAST_CITY, null)
            ?.let { lastCity ->
                currentLocationCity = CityData(lastCity, true, true)
                updateCitiesList()
            }
    }

    private fun loadFavoriteCities() {
        val cities = getSharedPreferences("favorite_cities", Context.MODE_PRIVATE)
            .getStringSet("cities", setOf()) ?: setOf()
        favoriteCities.clear()
        favoriteCities.addAll(cities.map { CityData(it, false) })
        updateCitiesList()
    }

    private fun updateCitiesList() {
        val lang = SharedPreferences.getLanguage(this)
        val updatedList = mutableListOf<CityData>().apply {
            currentLocationCity?.let {
                add(translateCityData(it, lang))
            }
            addAll(favoriteCities.filter { !it.isCurrentLocation }
                .map { translateCityData(it, lang) })
        }
        favoriteCities.clear()
        favoriteCities.addAll(updatedList)
        cityAdapter.notifyDataSetChanged()
    }

    private fun translateCityData(city: CityData, lang: String): CityData {
        val translatedName = when {
            lang == "uk" && !city.name.any { it.code in 0x0400..0x04FF } ->
                CityTranslations.translateToUkrainian(city.name)

            lang == "en" && city.name.any { it.code in 0x0400..0x04FF } ->
                CityTranslations.translateToEnglish(city.name)

            else -> city.name
        }
        return CityData(translatedName, city.isFavorite, city.isCurrentLocation)
    }

    private fun saveFavoriteCities() {
        getSharedPreferences("favorite_cities", Context.MODE_PRIVATE)
            .edit()
            .putStringSet("cities", favoriteCities
                .filter { !it.isCurrentLocation }
                .map { it.name }
                .toSet())
            .apply()
    }

    private fun removeCity(city: CityData) {
        favoriteCities.remove(city)
        cityAdapter.notifyDataSetChanged()
        saveFavoriteCities()
        Toast.makeText(this, R.string.toast_message_city_deleted, Toast.LENGTH_SHORT).show()
    }

    private fun addCityToFavorites(city: CityData) {
        val isDuplicate = favoriteCities.any { existingCity ->
            val existingCityEnglish = if (existingCity.name.any { it.code in 0x0400..0x04FF }) {
                CityTranslations.translateToEnglish(existingCity.name)
            } else {
                existingCity.name
            }

            val newCityEnglish = if (city.name.any { it.code in 0x0400..0x04FF }) {
                CityTranslations.translateToEnglish(city.name)
            } else {
                city.name
            }

            existingCityEnglish.equals(newCityEnglish, ignoreCase = true)
        }

        if (!isDuplicate) {
            val lang = SharedPreferences.getLanguage(this)
            val translatedCity = translateCityData(city, lang)
            favoriteCities.add(translatedCity)
            cityAdapter.notifyDataSetChanged()
            saveFavoriteCities()
            Toast.makeText(this, R.string.toast_message_city_added, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.toast_message_city_already_exists, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupKeyboardDismissal() {
        binding.root.setOnClickListener { hideKeyboard() }
    }

    private fun hideKeyboard() {
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        binding.searchView.clearFocus()
    }

    override fun dispatchTouchEvent(ev: android.view.MotionEvent): Boolean {
        if (ev.action == android.view.MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view != null && view != binding.searchView) {
                hideKeyboard()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}


