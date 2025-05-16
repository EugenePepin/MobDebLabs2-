package com.example.weatherapp
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.weatherapp.fragment.MainFragment
import com.example.weatherapp.utils.AnimationUtils
import com.example.weatherapp.utils.LocaleHelper.updateLocale
import com.example.weatherapp.utils.SharedPreferences


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<AnimationUtils>()
    private var mainFragment: MainFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val savedLanguage = SharedPreferences.getLanguage(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        splashScreenInit()
        updateLocale(this, savedLanguage)
        setContentView(R.layout.activity_main)

        mainFragment = MainFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, mainFragment!!)
            .commit()
    }

private fun splashScreenInit(){
    installSplashScreen().apply {
        setKeepOnScreenCondition {
            !viewModel.isReady.value
        }
    }
}

}