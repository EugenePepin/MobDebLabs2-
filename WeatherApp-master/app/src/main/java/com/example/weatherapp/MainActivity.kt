package com.example.weatherapp

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.weatherapp.fragment.MainFragment
import com.example.weatherapp.utils.AnimationUtils
import com.example.weatherapp.utils.SharedPreferences
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<AnimationUtils>()
    private var mainFragment: MainFragment? = null

    val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val savedLanguage = SharedPreferences.getLanguage(this)
            updateLocale(savedLanguage)
            mainFragment?.updateUI()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedLanguage = SharedPreferences.getLanguage(this)
        updateLocale(savedLanguage)
        
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !viewModel.isReady.value
            }
        }
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        
        mainFragment = MainFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, mainFragment!!)
            .commit()
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}