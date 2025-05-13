package com.example.weatherapp.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.android.volley.ClientError
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.MainActivity
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R
import com.example.weatherapp.SettingsActivity
import com.example.weatherapp.adapter.FragmentAdapter
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.utils.AnimationUtils
import com.example.weatherapp.utils.DialogManager
import com.example.weatherapp.utils.DialogManager.incorrectCityName
import com.example.weatherapp.utils.DialogManager.noConnection
import com.example.weatherapp.utils.SharedPreferences
import com.example.weatherapp.utils.WeatherCondition
import com.example.weatherapp.utils.WeatherCondition.rainConditions
import com.example.weatherapp.utils.WeatherCondition.snowConditions
import com.example.weatherapp.utils.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale


class MainFragment : Fragment() {
    private lateinit var lastUpdated: String
    private lateinit var clientLocation: FusedLocationProviderClient
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private lateinit var tabList: List<String>
    private val dataModel: MainViewModel by activityViewModels()
    private val fragmentList = listOf(
        HoursFragment.NewInstance(), DaysFragment.NewInstance()
    )
    private var lastCityOrLocation: String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater, container, false)
        isLocationEnabled()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        isLocationEnabled()
        checkLocationMessage()
        backgroundChange()
        updateCurrentCard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabList = listOf(
            getString(R.string.tabs_hours),
            getString(R.string.tabs_days)
        )
        super.onViewCreated(view, savedInstanceState)
        isLocationEnabled()
        checkPermission()
        backgroundChange()
        init()
        checkLocationMessage()
        updateCurrentCard()

    }



    private fun init() = with(binding) {
        clientLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = FragmentAdapter(activity as FragmentActivity, fragmentList)

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = tabList[pos]
        }.attach()
        //функіонал для кнопки синхронізації
        syncButton.setOnClickListener {
            tabLayout.selectTab(tabLayout.getTabAt(0))
            checkLocationMessage()
            AnimationUtils.startUpdateIconRotateAnimation(binding.syncButton)
        }

        //функіонал для поля з текстом
        editCityText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val cityName = editCityText.text.toString()
                requestCurrentWeatherData(cityName)
                backgroundChange()
                editCityText.setText("")

                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editCityText.windowToken, 0)

            }
            backgroundChange()
            true

        }

        binding.settingsIcon.setOnClickListener { view ->
            binding.dimSettingsBackground.visibility = View.VISIBLE
            val settingsMenu = PopupMenu(
                ContextThemeWrapper(requireContext(), R.style.Theme_Weather_App_Setting_PopUpMenu),
                view,
                Gravity.END,
                -5, 0
            )
            settingsMenu.menuInflater.inflate(R.menu.settings_menu, settingsMenu.menu)
            settingsMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_settings -> {
                        val intent = Intent(requireContext(), SettingsActivity::class.java)
                        (activity as MainActivity).settingsLauncher.launch(intent)
                        true
                    }
                    else -> false
                }
            }

            settingsMenu.setOnDismissListener {

                binding.dimSettingsBackground.visibility = View.GONE
            }

            binding.dimSettingsBackground.setOnClickListener {

                settingsMenu.dismiss()
            }

            settingsMenu.show()
        }
    }



    //функції для перевірки доступу до місця розташування

    private fun checkPermission() {
        if (!permissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }


    //додаємо місто з місця розташування
    private fun getLocation() {
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        clientLocation.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                val location = "${it.result.latitude},${it.result.longitude}"
                lastCityOrLocation = location
                requestCurrentWeatherData(location)
            }
    }

    //перевірка, чи включена функція місця розташування
    private fun isLocationEnabled(): Boolean {

        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)

    }

    //виведення AlertDialog при вимкненому розташуванні, і перекидування до налаштувань
    private fun checkLocationMessage() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }

    }

    private fun backgroundChange(){
        dataModel.liveDataCurrent.observe(viewLifecycleOwner) { weather ->
            val condition = weather.conditionStatusData.lowercase()
            val backgroundRes = WeatherCondition.getBackgroundForCondition(condition)


            if (rainConditions.any { condition.contains(it) }) {
                binding.animationView.visibility = View.VISIBLE
                binding.animationView.setAnimation(R.raw.rain_animation)
                binding.animationView.playAnimation()
            }
            else if (snowConditions.any { condition.contains(it) } ) {
                binding.animationView.visibility = View.VISIBLE
                binding.animationView.setAnimation(R.raw.snow_animation)
                binding.animationView.playAnimation()
            }
            else {
                binding.animationView.cancelAnimation()
                binding.animationView.visibility = View.GONE
            }

            if (backgroundRes != null) {
                AnimationUtils.smoothBackgroundChange(binding.imageView, binding.imageViewOverlay, backgroundRes)
            } else {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val outputFormat = SimpleDateFormat("HH", Locale.getDefault())
                val date = inputFormat.parse(lastUpdated)
                val currentHour = outputFormat.format(date).toInt()

                val fallbackRes = if (currentHour in 6..18) {
                    R.drawable.day_background
                } else {
                    R.drawable.night_background
                }
                AnimationUtils.smoothBackgroundChange(binding.imageView, binding.imageViewOverlay, fallbackRes)
            }
        }


    }




    //робимо запит до WeatherAPI

    private fun requestCurrentWeatherData(city: String) {
        lastCityOrLocation = city
        val lang = SharedPreferences.getLanguage(requireContext())
        val url =
            "https://api.weatherapi.com/v1/forecast.json?key=$API_KEY&q=$city&days=5&aqi=no&alerst=no&lang=$lang"
        val queue = Volley.newRequestQueue(context)
        val request = StringRequest(Request.Method.GET, url, { result ->
            try {

                val utf8Result = String(result.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
                parseWeatherData(utf8Result)
            } catch (e: Exception) {
                Log.e("WeatherApp", "Error parsing response: ${e.message}")
                parseWeatherData(result)
            }
        }, { error ->
            when (error) {
                is NoConnectionError -> {
                    noConnection(requireContext())
                }
                is ClientError -> {
                    incorrectCityName(requireContext())
                }
                else -> {
                    Log.d("VolleyError", "Volley error is: $error")
                }
            }
        })
        queue.add(request)
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentWeatherData(mainObject, list[0])
    }

    //витягаємо дані до картки з актуальною погодою
    private fun parseCurrentWeatherData(mainObject: JSONObject, weatherTempItem: WeatherData) {
        try {
            lastUpdated = mainObject.getJSONObject("current").getString("last_updated")
            sharedViewModel.lastUpdated = lastUpdated
            
            val locationName = mainObject.getJSONObject("location").getString("name")
            val conditionText = mainObject.getJSONObject("current").getJSONObject("condition").getString("text")
            
            val item = WeatherData(
                locationName,
                mainObject.getJSONObject("current").getString("last_updated"),
                conditionText,
                mainObject.getJSONObject("current").getString("temp_c").toFloat().toInt().toString(),
                mainObject.getJSONObject("current").getString("temp_f").toFloat().toInt().toString(),
                mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
                weatherTempItem.maxTempDataCelsius,
                weatherTempItem.minTempDataCelsius,
                weatherTempItem.maxTempDataFahrenheit,
                weatherTempItem.minTempDataFahrenheit,
                weatherTempItem.hoursData
            )
            dataModel.liveDataCurrent.value = item
            AnimationUtils.stopUpdateIconRotateAnimation(binding.syncButton)
        } catch (e: Exception) {
            Log.e("WeatherApp", "Error parsing weather data: ${e.message}")
        }
    }

    private val sharedViewModel: SharedViewModel by activityViewModels()
    class SharedViewModel : ViewModel() {
        var lastUpdated: String = ""
    }

    //витягаємо дані для днів
    private fun parseDays(mainObject: JSONObject): List<WeatherData> {
        val list = ArrayList<WeatherData>()
        val daysArray = mainObject.getJSONObject("forecast")
            .getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherData(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                "",
                "",
                day.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("maxtemp_f").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_f").toFloat().toInt().toString(),
                day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        dataModel.liveDataList.value = list
        return list

    }


    //додаємо дані до картки з актуальною погодою
    private fun updateCurrentCard() = with(binding) {
        dataModel.liveDataCurrent.observe(viewLifecycleOwner) {
            try {
                dateAndTimeTextView.text = it.dateAndTimeData
                    .substringBefore(" ")
                    .replace("-", "/")
                    .split("/")
                    .reversed()
                    .joinToString("/")

                cityNameTextView.text = it.cityNameData

                val isCelsius = SharedPreferences.getTemperatureUnit(requireContext())

                val currentTemp = if (isCelsius) it.currentTempDataCelsius else it.currentTempDataFahrenheit
                val maxTemp = if (isCelsius) it.maxTempDataCelsius else it.maxTempDataFahrenheit
                val minTemp = if (isCelsius) it.minTempDataCelsius else it.minTempDataFahrenheit

                currentTempTextView.text = currentTemp.ifEmpty { "$maxTemp/$minTemp" }
                tempMaxMinTextView.text = if (currentTemp.isEmpty()) "" else "$maxTemp/$minTemp°"
                temperatureUnitCurrentTemp.visibility = View.VISIBLE
                temperatureUnitCurrentTemp.text = if (isCelsius) "°c" else "°F"
                conditionStatusTextView.text = it.conditionStatusData

                if (tempMaxMinTextView.text.isEmpty()) {
                    tabLayout.selectTab(tabLayout.getTabAt(0))
                }

                backgroundChange()
                AnimationUtils.setReady()
            } catch (e: Exception) {
                Log.e("WeatherApp", "Error updating UI: ${e.message}")
            }
        }
    }

    fun updateUI() {
        binding.apply {
            tabList = listOf(
                getString(R.string.tabs_hours),
                getString(R.string.tabs_days)
            )
            for (i in 0 until tabLayout.tabCount) {
                tabLayout.getTabAt(i)?.text = tabList[i]
            }
            updateCurrentCard()

            lastCityOrLocation?.let { requestCurrentWeatherData(it) }
        }
    }




    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()

        //ключ до WeatherApi
        private const val API_KEY = BuildConfig.WEATHER_API_KEY

    }
}



