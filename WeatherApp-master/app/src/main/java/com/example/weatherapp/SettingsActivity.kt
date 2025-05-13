package com.example.weatherapp
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.weatherapp.utils.SharedPreferences
import java.util.Locale

class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val languageSpinner = findViewById<Spinner>(R.id.language_spinner)
        val tempUnitSpinner = findViewById<Spinner>(R.id.temp_unit_spinner)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.settings_dark_arrow)
        supportActionBar?.setHomeAsUpIndicator(upArrow)


        val currentLanguage = SharedPreferences.getLanguage(this)
        val languagePosition = if (currentLanguage == "uk") 1 else 0

        languageSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf(
                getString(R.string.setting_list_language_english),
                getString(R.string.setting_list_language_ukrainian)
            )
        )

        tempUnitSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf(
                getString(R.string.setting_list_temp_unit_C),
                getString(R.string.setting_list_temp_unit_F)
            )
        )
        tempUnitSpinner.setSelection(if (SharedPreferences.getTemperatureUnit(this)) 0 else 1)
        languageSpinner.setSelection(languagePosition)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val newLanguage = if (position == 0) "en" else "uk"
                if (newLanguage != SharedPreferences.getLanguage(this@SettingsActivity)) {
                    SharedPreferences.saveLanguage(this@SettingsActivity, newLanguage)
                    updateLocale(newLanguage)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        tempUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                SharedPreferences.saveTemperatureUnit(this@SettingsActivity, position == 0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_OK)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
