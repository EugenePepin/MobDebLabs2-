package com.example.weatherapp
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.weatherapp.utils.LocaleHelper
import com.example.weatherapp.utils.SharedPreferences


class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val savedLanguage = SharedPreferences.getLanguage(this)
        val context = LocaleHelper.updateLocale(this, savedLanguage)
        resources.updateConfiguration(context.resources.configuration, resources.displayMetrics)
        
        setContentView(R.layout.activity_settings)
        setupActionBar()
        languageSpinnerInit()
        tempUnitSpinnerInit()
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.settings_dark_arrow)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
    }

    private fun languageSpinnerInit() {
        val languageSpinner = findViewById<Spinner>(R.id.language_spinner)
        languageSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf(
                getString(R.string.setting_list_language_english),
                getString(R.string.setting_list_language_ukrainian)
            )
        )
        languageSpinner.setSelection(if (SharedPreferences.getLanguage(this) == "uk") 1 else 0)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedLang = if (position == 0) "en" else "uk"
                if (selectedLang != SharedPreferences.getLanguage(this@SettingsActivity)) {
                    SharedPreferences.saveLanguage(this@SettingsActivity, selectedLang)
                    val context = LocaleHelper.updateLocale(this@SettingsActivity, selectedLang)
                    resources.updateConfiguration(context.resources.configuration, resources.displayMetrics)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
    private fun tempUnitSpinnerInit() {
        val tempUnitSpinner = findViewById<Spinner>(R.id.temp_unit_spinner)
        tempUnitSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf(
                getString(R.string.setting_list_temp_unit_C),
                getString(R.string.setting_list_temp_unit_F)
            )
        )
        tempUnitSpinner.setSelection(if (SharedPreferences.getTemperatureUnit(this)) 0 else 1)

        tempUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                SharedPreferences.saveTemperatureUnit(this@SettingsActivity, position == 0)
                setResult(RESULT_OK)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
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
